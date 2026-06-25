import { feature } from 'topojson-client'

// CDN URLs for world-atlas data
const COUNTRIES_URL = 'https://cdn.jsdelivr.net/npm/world-atlas@2/countries-110m.json'

// Land mask grid for fast O(1) isLand() lookup
// Rows = latitude (-90..90), Cols = longitude (-180..180)
const MASK_LAT = 360 // 360 rows for latitude
const MASK_LNG = 720 // 720 columns for longitude
let landMask = null
let landPolygons = null // combined land MultiPolygon coordinates
let countryData = null // processed country features
let isLoading = false
let loadPromise = null

/**
 * Point-in-polygon using ray casting algorithm (standard crossing-number test).
 *
 * GeoJSON convention: coordinates are [longitude, latitude].
 * This function accepts (testLat, testLng) in geographic degrees.
 *
 * Ray casting logic:
 *   For each edge: if edge straddles the test latitude (y-direction),
 *   compute the intersection longitude (x) at that latitude.
 *   A point is inside if an odd number of edges cross to its left.
 */
function pointInMultiPolygon(testLat, testLng, multiPolygon) {
  for (const polygon of multiPolygon.coordinates) {
    const outerRing = polygon[0]
    if (!outerRing || outerRing.length < 3) continue

    // Quick bounding box reject using GeoJSON [lng, lat] convention
    let minLon = Infinity, maxLon = -Infinity
    let minLat = Infinity, maxLat = -Infinity
    for (const coord of outerRing) {
      // coord[0] = longitude, coord[1] = latitude
      if (coord[0] < minLon) minLon = coord[0]
      if (coord[0] > maxLon) maxLon = coord[0]
      if (coord[1] < minLat) minLat = coord[1]
      if (coord[1] > maxLat) maxLat = coord[1]
    }
    if (testLng < minLon || testLng > maxLon || testLat < minLat || testLat > maxLat) continue

    // Point-in-polygon: testLat is y, testLng is x
    let inside = false
    for (let i = 0, j = outerRing.length - 1; i < outerRing.length; j = i++) {
      // Edge endpoints: (xi=longitude, yi=latitude)
      const xi = outerRing[i][0], yi = outerRing[i][1]
      const xj = outerRing[j][0], yj = outerRing[j][1]

      // Check if edge straddles testLat (y-axis)
      if ((yi > testLat) !== (yj > testLat)) {
        // Compute intersection longitude at testLat
        const intersectX = xi + (xj - xi) * (testLat - yi) / (yj - yi)
        if (testLng < intersectX) {
          inside = !inside
        }
      }
    }

    if (!inside) continue

    // Check holes (inner rings) — if test point is inside a hole, it's outside land
    for (let k = 1; k < polygon.length; k++) {
      const hole = polygon[k]
      let insideHole = false
      for (let i = 0, j = hole.length - 1; i < hole.length; j = i++) {
        const xi = hole[i][0], yi = hole[i][1]
        const xj = hole[j][0], yj = hole[j][1]
        if ((yi > testLat) !== (yj > testLat)) {
          const intersectX = xi + (xj - xi) * (testLat - yi) / (yj - yi)
          if (testLng < intersectX) {
            insideHole = !insideHole
          }
        }
      }
      if (insideHole) return false
    }

    return true
  }
  return false
}

/**
 * Build a rasterized land mask from the land MultiPolygon for O(1) lookup.
 * Mask layout: rows = latitude (index 0 = -90°, index MASK_LAT-1 = +90°)
 *              cols = longitude (index 0 = -180°, index MASK_LNG-1 = +180°)
 */
function buildLandMask(landGeoJSON) {
  const mask = new Uint8Array(MASK_LAT * MASK_LNG)
  const multiPolygon = landGeoJSON

  // Test every cell directly for accuracy
  const stride = 2
  for (let latIdx = 0; latIdx < MASK_LAT; latIdx += stride) {
    // Map row index to geographic latitude (y-axis)
    const testLat = -90 + (latIdx / MASK_LAT) * 180
    for (let lngIdx = 0; lngIdx < MASK_LNG; lngIdx += stride) {
      // Map column index to geographic longitude (x-axis)
      const testLng = -180 + (lngIdx / MASK_LNG) * 360

      if (pointInMultiPolygon(testLat, testLng, multiPolygon)) {
        for (let di = 0; di < stride && latIdx + di < MASK_LAT; di++) {
          for (let dj = 0; dj < stride && lngIdx + dj < MASK_LNG; dj++) {
            mask[(latIdx + di) * MASK_LNG + (lngIdx + dj)] = 1
          }
        }
      }
    }
  }

  // Gap filling via dilation: if a cell has ≥3 land neighbors, mark as land
  for (let latIdx = 0; latIdx < MASK_LAT; latIdx++) {
    for (let lngIdx = 0; lngIdx < MASK_LNG; lngIdx++) {
      if (mask[latIdx * MASK_LNG + lngIdx] === 0) {
        let landNeighbors = 0
        if (latIdx > 0 && mask[(latIdx - 1) * MASK_LNG + lngIdx]) landNeighbors++
        if (latIdx < MASK_LAT - 1 && mask[(latIdx + 1) * MASK_LNG + lngIdx]) landNeighbors++
        if (lngIdx > 0 && mask[latIdx * MASK_LNG + (lngIdx - 1)]) landNeighbors++
        if (lngIdx < MASK_LNG - 1 && mask[latIdx * MASK_LNG + (lngIdx + 1)]) landNeighbors++
        if (landNeighbors >= 3) {
          mask[latIdx * MASK_LNG + lngIdx] = 1
        }
      }
    }
  }

  return mask
}

/**
 * Initialize and load geographic boundary data
 */
export async function loadGeoBoundaries() {
  if (landMask) return true
  if (isLoading) return loadPromise
  isLoading = true

  loadPromise = (async () => {
    try {
      console.log('[GeoBoundaries] Loading world-atlas data from CDN...')
      const response = await fetch(COUNTRIES_URL)
      if (!response.ok) throw new Error(`HTTP ${response.status}`)
      const topology = await response.json()
      console.log('[GeoBoundaries] Topology loaded, processing...')

      // Extract land as a combined MultiPolygon
      const countriesFeatureCollection = feature(topology, topology.objects.countries)
      countryData = countriesFeatureCollection.features

      // Build combined land MultiPolygon from all countries
      const allPolygons = []
      for (const feat of countryData) {
        if (feat.geometry.type === 'Polygon') {
          allPolygons.push(feat.geometry.coordinates)
        } else if (feat.geometry.type === 'MultiPolygon') {
          for (const coords of feat.geometry.coordinates) {
            allPolygons.push(coords)
          }
        }
      }

      landPolygons = { type: 'MultiPolygon', coordinates: allPolygons }

      // Build rasterized land mask for fast isLand() queries
      landMask = buildLandMask(landPolygons)

      console.log('[GeoBoundaries] Load complete. Countries:', countryData.length)
      return true
    } catch (e) {
      console.error('[GeoBoundaries] Failed to load:', e)
      return false
    } finally {
      isLoading = false
    }
  })()

  return loadPromise
}

/**
 * Fast O(1) check if a geographic point is on land using rasterized mask.
 * @param {number} lat - Latitude in degrees (-90 to 90)
 * @param {number} lng - Longitude in degrees (-180 to 180)
 */
export function isPointOnLand(lat, lng) {
  if (!landMask) return false

  // Clamp to valid ranges
  const clampedLat = Math.max(-90, Math.min(90, lat))
  const clampedLng = ((lng + 180) % 360) - 180

  // Map latitude to row index, longitude to column index
  const latIdx = Math.floor((clampedLat + 90) / 180 * MASK_LAT)
  const lngIdx = Math.floor((clampedLng + 180) / 360 * MASK_LNG)

  const r = Math.max(0, Math.min(MASK_LAT - 1, latIdx))
  const c = Math.max(0, Math.min(MASK_LNG - 1, lngIdx))

  return landMask[r * MASK_LNG + c] === 1
}

/**
 * Get all country features as GeoJSON Feature array
 */
export function getCountryFeatures() {
  return countryData || []
}

/**
 * Get continent boundary polygons grouped by continent name
 * Returns: { continentName: { type: 'MultiPolygon', coordinates: [...] } }
 */
export function getContinentPolygons() {
  if (!countryData) return {}

  const continentMap = {}

  for (const feat of countryData) {
    const continent = feat.properties.continent
    if (!continent) continue

    if (!continentMap[continent]) {
      continentMap[continent] = []
    }

    if (feat.geometry.type === 'Polygon') {
      continentMap[continent].push(feat.geometry.coordinates)
    } else if (feat.geometry.type === 'MultiPolygon') {
      for (const coords of feat.geometry.coordinates) {
        continentMap[continent].push(coords)
      }
    }
  }

  const result = {}
  for (const [name, coords] of Object.entries(continentMap)) {
    result[name] = { type: 'MultiPolygon', coordinates: coords }
  }

  return result
}

/**
 * Convert GeoJSON boundary polygon to a set of line segments for rendering.
 * Returns flat array of {x, y, z} world-space points for line-list rendering.
 */
export function geoPolygonToLineSegments(polygon, radius = 1) {
  const segments = []

  function processRing(ring) {
    const points = []
    for (const coord of ring) {
      // GeoJSON: coord[0] = longitude, coord[1] = latitude
      const lngRad = coord[0] * Math.PI / 180
      const latRad = coord[1] * Math.PI / 180
      const cosLat = Math.cos(latRad)
      const x = radius * cosLat * Math.cos(lngRad)
      const y = radius * Math.sin(latRad)
      const z = radius * cosLat * Math.sin(lngRad)
      points.push({ x, y, z })
    }

    for (let i = 0; i < points.length; i++) {
      const p1 = points[i]
      const p2 = points[(i + 1) % points.length]
      segments.push(p1.x, p1.y, p1.z)
      segments.push(p2.x, p2.y, p2.z)
    }
  }

  if (polygon.type === 'Polygon') {
    processRing(polygon.coordinates[0])
  } else if (polygon.type === 'MultiPolygon') {
    for (const poly of polygon.coordinates) {
      processRing(poly[0])
    }
  }

  return segments
}

/**
 * Generate vertex data for rendering filled continents using constrained triangulation.
 * Each convex polygon ring is triangulated via ear clipping for correct
 * rendering of concave country boundaries.
 */
export function geoPolygonToTriangleMesh(continentPolygon, radius = 1, baseColor = [0.5, 0.5, 0.5]) {
  const positions = []
  const colors = []

  function processRing(ring) {
    if (ring.length < 3) return

    // Convert GeoJSON [lng, lat] to 3D sphere points
    const pts = []
    for (const coord of ring) {
      const lngRad = coord[0] * Math.PI / 180
      const latRad = coord[1] * Math.PI / 180
      const cosLat = Math.cos(latRad)
      pts.push({
        x: radius * cosLat * Math.cos(lngRad),
        y: radius * Math.sin(latRad),
        z: radius * cosLat * Math.sin(lngRad)
      })
    }

    // For accuracy, use ear-clipping triangulation on the 2D (lng, lat) projection
    // This correctly handles concave polygons
    const coords2D = ring.map(c => ({ x: c[0], y: c[1] }))
    const triangles = earClipTriangulate(coords2D)

    if (triangles.length === 0) return

    for (const tri of triangles) {
      for (const idx of tri) {
        const p = pts[idx]
        positions.push(p.x, p.y, p.z)
        colors.push(baseColor[0], baseColor[1], baseColor[2])
      }
    }
  }

  if (continentPolygon.type === 'Polygon') {
    processRing(continentPolygon.coordinates[0])
  } else if (continentPolygon.type === 'MultiPolygon') {
    for (const poly of continentPolygon.coordinates) {
      processRing(poly[0])
    }
  }

  return { positions, colors }
}

/**
 * Ear-clipping triangulation for simple polygons (with or without concavities).
 * Works in 2D (longitude, latitude) space.
 * Returns array of triangle index triplets referencing the input array.
 */
function earClipTriangulate(vertices) {
  if (vertices.length < 3) return []
  const n = vertices.length
  const result = []
  const indices = vertices.map((_, i) => i)

  // Utility: signed area of triangle (a, b, c)
  function area(a, b, c) {
    return (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y)
  }

  // Is vertex at idx an ear tip?
  function isEar(prev, curr, next, remaining) {
    const a = vertices[prev], b = vertices[curr], c = vertices[next]
    // Must be convex (positive signed area for CCW winding)
    if (area(a, b, c) <= 0) return false
    // No other vertex may lie inside triangle (a, b, c)
    for (const vIdx of remaining) {
      if (vIdx === prev || vIdx === curr || vIdx === next) continue
      const p = vertices[vIdx]
      if (pointInTriangle(p, a, b, c)) return false
    }
    return true
  }

  // Is point p inside triangle (a, b, c)?
  function pointInTriangle(p, a, b, c) {
    const d1 = area(p, a, b)
    const d2 = area(p, b, c)
    const d3 = area(p, c, a)
    const hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0)
    const hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0)
    return !(hasNeg && hasPos)
  }

  let remaining = [...indices]
  let maxIter = 10 * n  // safety limit
  while (remaining.length > 3 && maxIter-- > 0) {
    let earFound = false
    for (let i = 0; i < remaining.length; i++) {
      const prev = remaining[(i - 1 + remaining.length) % remaining.length]
      const curr = remaining[i]
      const next = remaining[(i + 1) % remaining.length]

      if (isEar(prev, curr, next, remaining)) {
        result.push([prev, curr, next])
        remaining.splice(i, 1)
        earFound = true
        break
      }
    }
    if (!earFound) {
      // Fallback: remove a reflex vertex
      const curr = remaining[1]
      const prev = remaining[0]
      const next = remaining[2]
      result.push([prev, curr, next])
      remaining.splice(1, 1)
    }
  }

  // Final triangle
  if (remaining.length === 3) {
    result.push([remaining[0], remaining[1], remaining[2]])
  } else if (remaining.length > 3) {
    // Fan from first remaining vertex
    for (let i = 1; i < remaining.length - 1; i++) {
      result.push([remaining[0], remaining[i], remaining[i + 1]])
    }
  }

  return result
}