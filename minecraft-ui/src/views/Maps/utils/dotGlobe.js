const continentShapes = [
  {
    name: 'north-america',
    centerLat: 45,
    centerLng: -100,
    latExtent: 35,
    lngExtent: 50,
    rotation: -10,
    irregularity: 0.3
  },
  {
    name: 'south-america',
    centerLat: -15,
    centerLng: -60,
    latExtent: 35,
    lngExtent: 20,
    rotation: 15,
    irregularity: 0.35
  },
  {
    name: 'europe',
    centerLat: 52,
    centerLng: 15,
    latExtent: 15,
    lngExtent: 25,
    rotation: 0,
    irregularity: 0.4
  },
  {
    name: 'africa',
    centerLat: 5,
    centerLng: 20,
    latExtent: 35,
    lngExtent: 20,
    rotation: 5,
    irregularity: 0.3
  },
  {
    name: 'asia',
    centerLat: 40,
    centerLng: 95,
    latExtent: 30,
    lngExtent: 60,
    rotation: -5,
    irregularity: 0.35
  },
  {
    name: 'australia',
    centerLat: -25,
    centerLng: 135,
    latExtent: 15,
    lngExtent: 20,
    rotation: 10,
    irregularity: 0.3
  },
  {
    name: 'greenland',
    centerLat: 72,
    centerLng: -40,
    latExtent: 10,
    lngExtent: 15,
    rotation: -20,
    irregularity: 0.4
  },
  {
    name: 'antarctica',
    centerLat: -80,
    centerLng: 0,
    latExtent: 10,
    lngExtent: 180,
    rotation: 0,
    irregularity: 0.2,
    isPolar: true
  },
  {
    name: 'southeast-asia',
    centerLat: 5,
    centerLng: 110,
    latExtent: 15,
    lngExtent: 25,
    rotation: 0,
    irregularity: 0.5
  },
  {
    name: 'india',
    centerLat: 22,
    centerLng: 78,
    latExtent: 12,
    lngExtent: 12,
    rotation: -5,
    irregularity: 0.3
  },
  {
    name: 'middle-east',
    centerLat: 25,
    centerLng: 45,
    latExtent: 12,
    lngExtent: 15,
    rotation: 15,
    irregularity: 0.4
  },
  {
    name: 'central-america',
    centerLat: 15,
    centerLng: -85,
    latExtent: 10,
    lngExtent: 12,
    rotation: 30,
    irregularity: 0.4
  },
  {
    name: 'madagascar',
    centerLat: -20,
    centerLng: 47,
    latExtent: 8,
    lngExtent: 4,
    rotation: 10,
    irregularity: 0.3
  },
  {
    name: 'japan',
    centerLat: 37,
    centerLng: 138,
    latExtent: 8,
    lngExtent: 6,
    rotation: 20,
    irregularity: 0.4
  },
  {
    name: 'british-isles',
    centerLat: 54,
    centerLng: -4,
    latExtent: 6,
    lngExtent: 6,
    rotation: -10,
    irregularity: 0.4
  }
]

function hash2D(x, y) {
  const dot = x * 374761393 + y * 668265263
  const h = (dot ^ (dot >> 13)) * 1274126177
  return ((h ^ (h >> 16)) >>> 0) / 4294967295
}

function isLand(lat, lng) {
  if (lat < -75) return lat > -85
  if (lat > 80) return lat < 85
  
  const normalizedLng = ((lng + 180) % 360) - 180
  
  let landValue = 0
  
  for (const shape of continentShapes) {
    if (shape.isPolar) continue
    
    const rad = Math.PI / 180
    const rotRad = -shape.rotation * rad
    
    const cosRot = Math.cos(rotRad)
    const sinRot = Math.sin(rotRad)
    
    const latMid = shape.centerLat
    const lngMid = shape.centerLng
    
    let dLng = normalizedLng - lngMid
    if (dLng > 180) dLng -= 360
    if (dLng < -180) dLng += 360
    
    const latScale = Math.cos(lat * rad)
    const dx = (dLng * latScale) / shape.lngExtent
    const dy = (lat - latMid) / shape.latExtent
    
    const rx = dx * cosRot - dy * sinRot
    const ry = dx * sinRot + dy * cosRot
    
    const dist = Math.sqrt(rx * rx + ry * ry)
    
    const noiseVal = hash2D(
      Math.floor(lat * 5 + shape.centerLat),
      Math.floor(normalizedLng * 5 + shape.centerLng)
    )
    const threshold = 1.0 - shape.irregularity * (noiseVal - 0.5) * 2
    
    if (dist < threshold) {
      const edgeSoftness = 0.15
      const edgeFactor = Math.min(1, (threshold - dist) / edgeSoftness)
      landValue = Math.max(landValue, edgeFactor)
    }
  }
  
  const detailNoise = hash2D(Math.floor(lat * 20), Math.floor(normalizedLng * 20))
  const detailThreshold = 0.4
  if (landValue > 0.3 && landValue < 0.7) {
    if (detailNoise < detailThreshold && landValue < 0.5) {
      landValue *= 0.5
    }
  }
  
  return landValue > 0.45
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
 * Map continent shape names to continent data IDs
 */
const shapeToContinentId = {
  'north-america': 'north-america',
  'south-america': 'south-america',
  'europe': 'europe',
  'africa': 'africa',
  'asia': 'asia',
  'australia': 'oceania',
  'southeast-asia': 'asia',
  'india': 'asia',
  'middle-east': 'asia',
  'central-america': 'north-america',
  'madagascar': 'africa',
  'japan': 'asia',
  'british-isles': 'europe',
  'greenland': 'north-america',
  'antarctica': 'antarctica'
}

/**
 * Find which continent a lat/lng coordinate belongs to
 * @param {number} lat
 * @param {number} lng
 * @returns {string|null} continent ID or null (ocean)
 */
export function findContinentAt(lat, lng) {
  if (lat < -75) return 'antarctica'
  if (lat > 80) return null

  const normalizedLng = ((lng + 180) % 360) - 180
  const rad = Math.PI / 180

  let bestContinent = null
  let bestLandValue = 0

  for (const shape of continentShapes) {
    if (shape.isPolar) continue

    const rotRad = -shape.rotation * rad
    const cosRot = Math.cos(rotRad)
    const sinRot = Math.sin(rotRad)

    const latMid = shape.centerLat
    const lngMid = shape.centerLng

    let dLng = normalizedLng - lngMid
    if (dLng > 180) dLng -= 360
    if (dLng < -180) dLng += 360

    const latScale = Math.cos(lat * rad)
    const dx = (dLng * latScale) / shape.lngExtent
    const dy = (lat - latMid) / shape.latExtent

    const rx = dx * cosRot - dy * sinRot
    const ry = dx * sinRot + dy * cosRot

    const dist = Math.sqrt(rx * rx + ry * ry)

    const noiseVal = hash2D(
      Math.floor(lat * 5 + shape.centerLat),
      Math.floor(normalizedLng * 5 + shape.centerLng)
    )
    const threshold = 1.0 - shape.irregularity * (noiseVal - 0.5) * 2

    if (dist < threshold) {
      const edgeSoftness = 0.15
      const edgeFactor = Math.min(1, (threshold - dist) / edgeSoftness)
      if (edgeFactor > bestLandValue) {
        bestLandValue = edgeFactor
        bestContinent = shapeToContinentId[shape.name] || shape.name
      }
    }
  }

  if (bestLandValue > 0.45) {
    return bestContinent
  }

  // Check polar regions
  if (lat < -75 && lat > -85) return 'antarctica'

  return null
}

/**
 * Generate circular dot geometry for each land point.
 * Each dot is a quad with UV coordinates for fragment-shader-based circle clipping.
 * Uses terrain-aware coloring for better geographic detail representation.
 */
export function generateDotQuadData(radius = 1, dotSize = 0.0018, latSegments = 220, lngSegments = 400) {
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

        // Compute tangent and bitangent
        let tx, ty, tz
        if (Math.abs(ny) < 0.999) {
          tx = nz
          ty = 0
          tz = -nx
        } else {
          tx = 1
          ty = 0
          tz = 0
        }
        const lenT = Math.sqrt(tx * tx + ty * ty + tz * tz)
        tx /= lenT; ty /= lenT; tz /= lenT

        // Bitangent = cross(normal, tangent)
        const bx = ny * tz - nz * ty
        const by = nz * tx - nx * tz
        const bz = nx * ty - ny * tx

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

    // Tangent basis
    let tx, ty, tz
    if (Math.abs(ny) < 0.999) {
      tx = nz; ty = 0; tz = -nx
    } else {
      tx = 1; ty = 0; tz = 0
    }
    const lenT = Math.sqrt(tx * tx + ty * ty + tz * tz)
    tx /= lenT; ty /= lenT; tz /= lenT

    // Bitangent
    const bx = ny * tz - nz * ty
    const by = nz * tx - nx * tz
    const bz = nx * ty - ny * tx

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
