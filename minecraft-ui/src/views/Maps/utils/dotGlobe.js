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

export function generateGlowMarkerData(markers, radius = 1) {
  const positions = []
  const colors = []
  
  for (const marker of markers) {
    const latRad = marker.lat * Math.PI / 180
    const lngRad = marker.lng * Math.PI / 180
    const cosLat = Math.cos(latRad)
    
    const x = radius * cosLat * Math.cos(lngRad)
    const y = radius * Math.sin(latRad)
    const z = radius * cosLat * Math.sin(lngRad)
    
    positions.push(x, y, z)
    colors.push(marker.color[0], marker.color[1], marker.color[2])
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
