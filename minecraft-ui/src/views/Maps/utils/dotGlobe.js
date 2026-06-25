import { isPointOnLand, getCountryFeatures } from './geoBoundaries.js'

function hash2D(x, y) {
  const dot = x * 374761393 + y * 668265263
  const h = (dot ^ (dot >> 13)) * 1274126177
  return ((h ^ (h >> 16)) >>> 0) / 4294967295
}

function isLand(lat, lng) {
  // Use real geographic boundary data for accurate land/water detection
  return isPointOnLand(lat, lng)
}

export function generateDotGlobeData(radius = 1, latSegments = 120, lngSegments = 240) {
  const positions = []
  const colors = []
  const sizes = []
  
  for (let latIdx = 1; latIdx < latSegments; latIdx++) {
    const lat = 90 - (latIdx / latSegments) * 180
    
    const latRad = lat * Math.PI / 180
    const circumference = Math.cos(latRad) * lngSegments
    const count = Math.max(2, Math.floor(circumference))
    
    for (let lngIdx = 0; lngIdx < count; lngIdx++) {
      const lng = -180 + (lngIdx / count) * 360
      
      if (isLand(lat, lng)) {
        const latRadP = lat * Math.PI / 180
        const lngRadP = lng * Math.PI / 180
        
        const cosLat = Math.cos(latRadP)
        const x = radius * cosLat * Math.cos(lngRadP)
        const y = radius * Math.sin(latRadP)
        const z = radius * cosLat * Math.sin(lngRadP)
        
        positions.push(x, y, z)
        
        const noise = hash2D(latIdx * 3, lngIdx * 7)
        const brightness = 0.85 + noise * 0.15
        colors.push(brightness, brightness, brightness)
        
        const sizeVar = 0.8 + noise * 0.4
        sizes.push(sizeVar)
      }
    }
  }
  
  const vertexData = new Float32Array(positions.length + colors.length)
  let offset = 0
  for (let i = 0; i < positions.length / 3; i++) {
    vertexData[offset++] = positions[i * 3]
    vertexData[offset++] = positions[i * 3 + 1]
    vertexData[offset++] = positions[i * 3 + 2]
    vertexData[offset++] = colors[i * 3]
    vertexData[offset++] = colors[i * 3 + 1]
    vertexData[offset++] = colors[i * 3 + 2]
  }
  
  return {
    positions,
    colors,
    vertexData,
    count: positions.length / 3
  }
}

/**
 * Map continent names from world-atlas data to our internal IDs
 */
const worldAtlasContinentMap = {
  'Asia': 'asia',
  'Europe': 'europe',
  'Africa': 'africa',
  'North America': 'north-america',
  'South America': 'south-america',
  'Oceania': 'oceania',
  'Antarctica': 'antarctica'
}

/**
 * Find which continent a lat/lng coordinate belongs to using real geographic boundary data
 * @param {number} lat
 * @param {number} lng
 * @returns {string|null} continent ID or null (ocean)
 */
export function findContinentAt(lat, lng) {
  const countries = getCountryFeatures()
  if (!countries || countries.length === 0) return null

  for (const feat of countries) {
    if (isPointInFeature(lat, lng, feat)) {
      const continent = feat.properties.continent
      return worldAtlasContinentMap[continent] || continent?.toLowerCase().replace(/\s+/g, '-') || null
    }
  }
  return null
}

/**
 * Check if a point is inside a GeoJSON feature (supports Polygon and MultiPolygon)
 * Uses standard ray casting: testLat = y-axis, testLng = x-axis
 * GeoJSON: coord[0] = longitude, coord[1] = latitude
 */
function isPointInFeature(testLat, testLng, feature) {
  if (!feature || !feature.geometry) return false
  
  const coords = feature.geometry.type === 'Polygon'
    ? [feature.geometry.coordinates]
    : feature.geometry.coordinates

  for (const polygon of coords) {
    const outerRing = polygon[0]
    if (!outerRing || outerRing.length < 3) continue

    // Quick bounding box check [lng, lat] convention
    let minLon = Infinity, maxLon = -Infinity
    let minLat = Infinity, maxLat = -Infinity
    for (const c of outerRing) {
      if (c[0] < minLon) minLon = c[0]
      if (c[0] > maxLon) maxLon = c[0]
      if (c[1] < minLat) minLat = c[1]
      if (c[1] > maxLat) maxLat = c[1]
    }
    if (testLng < minLon || testLng > maxLon || testLat < minLat || testLat > maxLat) continue

    // Point-in-polygon ray casting
    let inside = false
    for (let i = 0, j = outerRing.length - 1; i < outerRing.length; j = i++) {
      const xi = outerRing[i][0], yi = outerRing[i][1]
      const xj = outerRing[j][0], yj = outerRing[j][1]
      if ((yi > testLat) !== (yj > testLat)) {
        const intersectX = xi + (xj - xi) * (testLat - yi) / (yj - yi)
        if (testLng < intersectX) {
          inside = !inside
        }
      }
    }
    if (!inside) continue

    // Check holes
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
 * Generate circular dot geometry for each land point.
 * Each dot is a quad with UV coordinates for fragment-shader-based circle clipping.
 * Uses terrain-aware coloring for better geographic detail representation.
 */
// 生成每个陆地点的四边形几何数据
export function generateDotQuadData(radius = 1, dotSize = 0.0010, latSegments = 420, lngSegments = 600) {
  const positions = []
  const uvs = []
  const colors = []
  const indices = []
  let vertexOffset = 0

  for (let latIdx = 1; latIdx < latSegments; latIdx++) {
    const lat = 90 - (latIdx / latSegments) * 180
    const latRad = lat * Math.PI / 180
    const circumference = Math.cos(latRad) * lngSegments
    const count = Math.max(2, Math.floor(circumference))

    for (let lngIdx = 0; lngIdx < count; lngIdx++) {
      const lng = -180 + (lngIdx / count) * 360

      if (isLand(lat, lng)) {
        const latRadP = lat * Math.PI / 180
        const lngRadP = lng * Math.PI / 180
        const cosLat = Math.cos(latRadP)

        const cx = radius * cosLat * Math.cos(lngRadP)
        const cy = radius * Math.sin(latRadP)
        const cz = radius * cosLat * Math.sin(lngRadP)

        // Normal = position (on unit sphere)
        const nx = cx / radius
        const ny = cy / radius
        const nz = cz / radius

        // Compute tangent and bitangent using spherical coordinate basis
        // This provides a smooth, consistent frame field across the entire sphere,
        // eliminating unnatural dot deformation during rotation.
        // T = eastward (longitude) tangent: (-sin(lng), 0, cos(lng))
        // B = northward (latitude) tangent: (-sin(lat)*cos(lng), cos(lat), -sin(lat)*sin(lng))
        // Both are unit vectors and mutually perpendicular.
        const tx = -Math.sin(lngRadP)
        const ty = 0
        const tz = Math.cos(lngRadP)

        const bx = -Math.sin(latRadP) * Math.cos(lngRadP)
        const by = Math.cos(latRadP)
        const bz = -Math.sin(latRadP) * Math.sin(lngRadP)

        // Terrain-aware parameters
        const noise = hash2D(latIdx * 3, lngIdx * 7)
        const terrainNoise1 = hash2D(latIdx * 17, lngIdx * 31)
        const terrainNoise2 = hash2D(latIdx * 11, lngIdx * 23)
        const elevation = terrainNoise1 * 0.6 + terrainNoise2 * 0.4
        const latAbs = Math.abs(lat)

        // Variable dot size based on "elevation" for 3D terrain feel
        const sizeScale = 0.75 + elevation * 0.5
        const hSize = (dotSize * sizeScale) / 2

        // White color
        const r = 1.0
        const g = 1.0
        const b = 1.0

        // UV coordinates for circular clipping: range [-1, 1] per vertex
        const vertUvs = [
          [-1, -1], [-1, 1], [1, -1], [1, 1]
        ]

        // 4 vertices of quad with UVs
        const verts = [
          { x: cx + (-tx - bx) * hSize, y: cy + (-ty - by) * hSize, z: cz + (-tz - bz) * hSize },
          { x: cx + (-tx + bx) * hSize, y: cy + (-ty + by) * hSize, z: cz + (-tz + bz) * hSize },
          { x: cx + (tx - bx) * hSize, y: cy + (ty - by) * hSize, z: cz + (tz - bz) * hSize },
          { x: cx + (tx + bx) * hSize, y: cy + (ty + by) * hSize, z: cz + (tz + bz) * hSize }
        ]

        for (let vi = 0; vi < 4; vi++) {
          const v = verts[vi]
          positions.push(v.x, v.y, v.z)
          uvs.push(vertUvs[vi][0], vertUvs[vi][1])
          colors.push(r, g, b)
        }

        indices.push(
          vertexOffset, vertexOffset + 1, vertexOffset + 2,
          vertexOffset + 1, vertexOffset + 3, vertexOffset + 2
        )
        vertexOffset += 4
      }
    }
  }

  // Vertex layout: position(3) + uv(2) + color(3) = 8 floats = 32 bytes
  const vertexData = new Float32Array(positions.length + uvs.length + colors.length)
  let offset = 0
  for (let i = 0; i < positions.length / 3; i++) {
    vertexData[offset++] = positions[i * 3]
    vertexData[offset++] = positions[i * 3 + 1]
    vertexData[offset++] = positions[i * 3 + 2]
    vertexData[offset++] = uvs[i * 2]
    vertexData[offset++] = uvs[i * 2 + 1]
    vertexData[offset++] = colors[i * 3]
    vertexData[offset++] = colors[i * 3 + 1]
    vertexData[offset++] = colors[i * 3 + 2]
  }

  return {
    vertexData,
    indexData: new Uint32Array(indices),
    positionCount: positions.length / 3,
    indexCount: indices.length
  }
}

/**
 * Generate quad-based glow marker geometry for continent/country markers.
 * Each marker is rendered as a larger glowing quad floating above the surface.
 */
export function generateGlowMarkerData(markers, radius = 1.03, baseSize = 0.035) {
  const positions = []
  const colors = []
  const indices = []
  const markerMeta = []
  let vertexOffset = 0

  for (const marker of markers) {
    const latRad = marker.lat * Math.PI / 180
    const lngRad = marker.lng * Math.PI / 180
    const cosLat = Math.cos(latRad)

    const cx = radius * cosLat * Math.cos(lngRad)
    const cy = radius * Math.sin(latRad)
    const cz = radius * cosLat * Math.sin(lngRad)

    // Normal at this point
    const nx = cx / radius
    const ny = cy / radius
    const nz = cz / radius

    // Tangent basis using spherical coordinate frame
    const tx = -Math.sin(lngRad)
    const ty = 0
    const tz = Math.cos(lngRad)

    const bx = -Math.sin(latRad) * Math.cos(lngRad)
    const by = Math.cos(latRad)
    const bz = -Math.sin(latRad) * Math.sin(lngRad)

    const size = baseSize * (marker.size || 1.0)
    const hSize = size / 2

    // 4 vertices of quad
    const verts = [
      { x: cx + (-tx - bx) * hSize, y: cy + (-ty - by) * hSize, z: cz + (-tz - bz) * hSize },
      { x: cx + (-tx + bx) * hSize, y: cy + (-ty + by) * hSize, z: cz + (-tz + bz) * hSize },
      { x: cx + (tx - bx) * hSize, y: cy + (ty - by) * hSize, z: cz + (tz - bz) * hSize },
      { x: cx + (tx + bx) * hSize, y: cy + (ty + by) * hSize, z: cz + (tz + bz) * hSize }
    ]

    const [r, g, b] = marker.color

    for (const v of verts) {
      positions.push(v.x, v.y, v.z)
      colors.push(r, g, b)
    }

    indices.push(
      vertexOffset, vertexOffset + 1, vertexOffset + 2,
      vertexOffset + 1, vertexOffset + 3, vertexOffset + 2
    )

    // Store marker reference for this quad
    markerMeta.push({
      id: marker.id,
      type: marker.type,
      name: marker.name,
      nameEn: marker.nameEn,
      lat: marker.lat,
      lng: marker.lng,
      color: marker.color,
      worldPos: { x: cx, y: cy, z: cz }
    })

    vertexOffset += 4
  }

  const vertexData = new Float32Array(positions.length + colors.length)
  let offset = 0
  for (let i = 0; i < positions.length / 3; i++) {
    vertexData[offset++] = positions[i * 3]
    vertexData[offset++] = positions[i * 3 + 1]
    vertexData[offset++] = positions[i * 3 + 2]
    vertexData[offset++] = colors[i * 3]
    vertexData[offset++] = colors[i * 3 + 1]
    vertexData[offset++] = colors[i * 3 + 2]
  }

  return {
    vertexData,
    indexData: new Uint32Array(indices),
    positionCount: positions.length / 3,
    indexCount: indices.length,
    markerMeta
  }
}
