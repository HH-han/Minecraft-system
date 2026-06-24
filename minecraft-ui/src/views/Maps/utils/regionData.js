const continentColors = {
  asia: { main: [0.95, 0.55, 0.35], name: '亚洲' },
  europe: { main: [0.35, 0.65, 0.95], name: '欧洲' },
  africa: { main: [0.85, 0.65, 0.25], name: '非洲' },
  'north-america': { main: [0.45, 0.75, 0.55], name: '北美洲' },
  'south-america': { main: [0.75, 0.45, 0.65], name: '南美洲' },
  oceania: { main: [0.55, 0.85, 0.75], name: '大洋洲' },
  antarctica: { main: [0.85, 0.85, 0.85], name: '南极洲' }
}

function hash2D(x, y) {
  const dot = x * 374761393 + y * 668265263
  const h = (dot ^ (dot >> 13)) * 1274126177
  return ((h ^ (h >> 16)) >>> 0) / 4294967295
}

function perturbPoint(lat, lng, strength = 0.5) {
  const h1 = hash2D(Math.floor(lat * 10), Math.floor(lng * 10))
  const h2 = hash2D(Math.floor(lng * 10), Math.floor(lat * 10))
  const angle = h1 * Math.PI * 2
  const dist = h2 * strength
  return {
    lat: lat + Math.sin(angle) * dist,
    lng: lng + Math.cos(angle) * dist
  }
}

function createContinentBoundary(continent) {
  const points = []
  const { centerLat, centerLng, latExtent, lngExtent, irregularity } = continent
  
  const numPoints = 32
  for (let i = 0; i < numPoints; i++) {
    const baseAngle = (i / numPoints) * Math.PI * 2
    const h = hash2D(i * 3 + centerLat, i * 5 + centerLng)
    const radiusVariation = 1 - irregularity * 0.5 + h * irregularity
    const latRadius = latExtent * radiusVariation
    const lngRadius = lngExtent * radiusVariation
    
    let lat = centerLat + Math.sin(baseAngle) * latRadius
    let lng = centerLng + Math.cos(baseAngle) * lngRadius
    
    const perturbed = perturbPoint(lat, lng, irregularity * 3)
    lat = perturbed.lat
    lng = perturbed.lng
    
    const edgeSoftness = 0.3
    const edgeNoise = hash2D(Math.floor(lat * 20), Math.floor(lng * 20))
    if (edgeNoise < edgeSoftness) {
      const scale = edgeSoftness / (edgeNoise + 0.01)
      lat = centerLat + (lat - centerLat) * scale * 0.5
      lng = centerLng + (lng - centerLng) * scale * 0.5
    }
    
    points.push({ lat, lng })
  }
  
  return points
}

function createCountryBoundaries() {
  return {
    cn: { continent: 'asia', lat: 35, lng: 105, latExtent: 15, lngExtent: 18, irregularity: 0.4 },
    us: { continent: 'north-america', lat: 38, lng: -97, latExtent: 12, lngExtent: 25, irregularity: 0.35 },
    ru: { continent: 'europe', lat: 55, lng: 80, latExtent: 20, lngExtent: 60, irregularity: 0.4 },
    br: { continent: 'south-america', lat: -10, lng: -55, latExtent: 20, lngExtent: 18, irregularity: 0.35 },
    au: { continent: 'oceania', lat: -25, lng: 135, latExtent: 12, lngExtent: 18, irregularity: 0.4 },
    in: { continent: 'asia', lat: 20, lng: 78, latExtent: 12, lngExtent: 12, irregularity: 0.3 },
    jp: { continent: 'asia', lat: 36, lng: 138, latExtent: 6, lngExtent: 6, irregularity: 0.35 },
    de: { continent: 'europe', lat: 51, lng: 10, latExtent: 4, lngExtent: 6, irregularity: 0.3 },
    gb: { continent: 'europe', lat: 54, lng: -2, latExtent: 3, lngExtent: 4, irregularity: 0.3 },
    fr: { continent: 'europe', lat: 46, lng: 2, latExtent: 6, lngExtent: 6, irregularity: 0.3 },
    za: { continent: 'africa', lat: -28, lng: 25, latExtent: 8, lngExtent: 8, irregularity: 0.35 },
    eg: { continent: 'africa', lat: 26, lng: 30, latExtent: 6, lngExtent: 6, irregularity: 0.3 },
    mx: { continent: 'north-america', lat: 24, lng: -102, latExtent: 10, lngExtent: 15, irregularity: 0.35 },
    ca: { continent: 'north-america', lat: 56, lng: -106, latExtent: 15, lngExtent: 30, irregularity: 0.4 },
    ar: { continent: 'south-america', lat: -34, lng: -65, latExtent: 12, lngExtent: 12, irregularity: 0.35 },
    kz: { continent: 'asia', lat: 48, lng: 67, latExtent: 10, lngExtent: 25, irregularity: 0.35 },
    sa: { continent: 'asia', lat: 24, lng: 45, latExtent: 8, lngExtent: 15, irregularity: 0.3 },
    id: { continent: 'asia', lat: -5, lng: 120, latExtent: 12, lngExtent: 20, irregularity: 0.4 },
    kr: { continent: 'asia', lat: 36, lng: 128, latExtent: 5, lngExtent: 5, irregularity: 0.3 },
    tr: { continent: 'asia', lat: 39, lng: 35, latExtent: 8, lngExtent: 15, irregularity: 0.35 }
  }
}

function generatePolygonMesh(points, radius = 1, segments = 32) {
  const positions = []
  const normals = []
  const colors = []
  const indices = []
  
  const centerLat = 0
  const centerLng = 0
  
  positions.push(0, 0, 0)
  normals.push(0, 0, 1)
  colors.push(0.5, 0.5, 0.5)
  
  for (let i = 0; i < points.length; i++) {
    const p = points[i]
    const latRad = p.lat * Math.PI / 180
    const lngRad = p.lng * Math.PI / 180
    const cosLat = Math.cos(latRad)
    const x = radius * cosLat * Math.cos(lngRad)
    const y = radius * Math.sin(latRad)
    const z = radius * cosLat * Math.sin(lngRad)
    positions.push(x, y, z)
    normals.push(0, 0, 1)
    colors.push(0.5, 0.5, 0.5)
  }
  
  for (let i = 1; i <= points.length; i++) {
    const next = i === points.length ? 1 : i + 1
    indices.push(0, i, next)
  }
  
  return { positions, normals, colors, indices }
}

function generateRegionMesh(region, radius = 1, baseColor, colorVariation = 0.15) {
  const points = []
  const { centerLat, centerLng, latExtent, lngExtent, irregularity } = region
  
  const numPoints = 28
  for (let i = 0; i < numPoints; i++) {
    const baseAngle = (i / numPoints) * Math.PI * 2
    const h = hash2D(i * 3 + centerLat, i * 5 + centerLng)
    const radiusVariation = 1 - irregularity * 0.5 + h * irregularity
    const latRadius = latExtent * radiusVariation
    const lngRadius = lngExtent * radiusVariation
    
    let lat = centerLat + Math.sin(baseAngle) * latRadius
    let lng = centerLng + Math.cos(baseAngle) * lngRadius
    
    const perturbed = perturbPoint(lat, lng, irregularity * 2)
    lat = perturbed.lat
    lng = perturbed.lng
    
    points.push({ lat, lng })
  }
  
  const positions = []
  const normals = []
  const colors = []
  const indices = []
  
  positions.push(0, 0, 0)
  normals.push(0, 0, 1)
  
  const colorH = hash2D(centerLat * 7, centerLng * 11)
  const r = Math.min(1, Math.max(0, baseColor[0] + (colorH - 0.5) * colorVariation))
  const g = Math.min(1, Math.max(0, baseColor[1] + (hash2D(centerLng * 13, centerLat * 17) - 0.5) * colorVariation))
  const b = Math.min(1, Math.max(0, baseColor[2] + (hash2D(centerLat * 19, centerLng * 23) - 0.5) * colorVariation))
  colors.push(r, g, b)
  
  for (let i = 0; i < points.length; i++) {
    const p = points[i]
    const latRad = p.lat * Math.PI / 180
    const lngRad = p.lng * Math.PI / 180
    const cosLat = Math.cos(latRad)
    const x = radius * cosLat * Math.cos(lngRad)
    const y = radius * Math.sin(latRad)
    const z = radius * cosLat * Math.sin(lngRad)
    positions.push(x, y, z)
    normals.push(0, 0, 1)
    
    const vertColorH = hash2D(p.lat * 13, p.lng * 17)
    const vr = Math.min(1, Math.max(0, baseColor[0] + (vertColorH - 0.5) * colorVariation * 1.5))
    const vg = Math.min(1, Math.max(0, baseColor[1] + (hash2D(p.lng * 19, p.lat * 29) - 0.5) * colorVariation * 1.5))
    const vb = Math.min(1, Math.max(0, baseColor[2] + (hash2D(p.lat * 31, p.lng * 37) - 0.5) * colorVariation * 1.5))
    colors.push(vr, vg, vb)
  }
  
  for (let i = 1; i <= points.length; i++) {
    const next = i === points.length ? 1 : i + 1
    indices.push(0, i, next)
  }
  
  return { positions, normals, colors, indices }
}

export function generateContinentGlobeData(radius = 1, continents) {
  const allPositions = []
  const allNormals = []
  const allColors = []
  const allIndices = []
  
  let indexOffset = 0
  
  for (const continent of continents) {
    const color = continentColors[continent.id]?.main || [0.7, 0.7, 0.7]
    const mesh = generateRegionMesh(continent, radius, color, 0.12)
    
    allPositions.push(...mesh.positions)
    allNormals.push(...mesh.normals)
    allColors.push(...mesh.colors)
    
    for (const idx of mesh.indices) {
      allIndices.push(idx + indexOffset)
    }
    indexOffset += mesh.positions.length / 3
  }
  
  const vertexData = new Float32Array(allPositions.length / 3 * 6)
  for (let i = 0; i < allPositions.length / 3; i++) {
    const offset = i * 6
    vertexData[offset] = allPositions[i * 3]
    vertexData[offset + 1] = allPositions[i * 3 + 1]
    vertexData[offset + 2] = allPositions[i * 3 + 2]
    vertexData[offset + 3] = allColors[i * 3]
    vertexData[offset + 4] = allColors[i * 3 + 1]
    vertexData[offset + 5] = allColors[i * 3 + 2]
  }
  
  return {
    vertexData,
    indexData: new Uint32Array(allIndices),
    count: allIndices.length
  }
}

export function generateCountryGlobeData(radius = 1) {
  const countries = createCountryBoundaries()
  const allPositions = []
  const allNormals = []
  const allColors = []
  const allIndices = []
  
  let indexOffset = 0
  
  for (const [id, region] of Object.entries(countries)) {
    const continentColor = continentColors[region.continent]?.main || [0.7, 0.7, 0.7]
    const mesh = generateRegionMesh(region, radius, continentColor, 0.2)
    
    allPositions.push(...mesh.positions)
    allNormals.push(...mesh.normals)
    allColors.push(...mesh.colors)
    
    for (const idx of mesh.indices) {
      allIndices.push(idx + indexOffset)
    }
    indexOffset += mesh.positions.length / 3
  }
  
  const vertexData = new Float32Array(allPositions.length / 3 * 6)
  for (let i = 0; i < allPositions.length / 3; i++) {
    const offset = i * 6
    vertexData[offset] = allPositions[i * 3]
    vertexData[offset + 1] = allPositions[i * 3 + 1]
    vertexData[offset + 2] = allPositions[i * 3 + 2]
    vertexData[offset + 3] = allColors[i * 3]
    vertexData[offset + 4] = allColors[i * 3 + 1]
    vertexData[offset + 5] = allColors[i * 3 + 2]
  }
  
  return {
    vertexData,
    indexData: new Uint32Array(allIndices),
    count: allIndices.length
  }
}

export function generateBoundaryLinesData(radius = 1, regions) {
  const positions = []
  const colors = []
  
  for (const region of regions) {
    const points = []
    const { centerLat, centerLng, latExtent, lngExtent, irregularity } = region
    const numPoints = 32
    
    for (let i = 0; i < numPoints; i++) {
      const baseAngle = (i / numPoints) * Math.PI * 2
      const h = hash2D(i * 3 + centerLat, i * 5 + centerLng)
      const radiusVariation = 1 - irregularity * 0.5 + h * irregularity
      const latRadius = latExtent * radiusVariation
      const lngRadius = lngExtent * radiusVariation
      
      let lat = centerLat + Math.sin(baseAngle) * latRadius
      let lng = centerLng + Math.cos(baseAngle) * lngRadius
      
      const perturbed = perturbPoint(lat, lng, irregularity * 2)
      lat = perturbed.lat
      lng = perturbed.lng
      
      const latRad = lat * Math.PI / 180
      const lngRad = lng * Math.PI / 180
      const cosLat = Math.cos(latRad)
      
      const scale = 1.001
      const x = scale * radius * cosLat * Math.cos(lngRad)
      const y = scale * radius * Math.sin(latRad)
      const z = scale * radius * cosLat * Math.sin(lngRad)
      
      points.push({ x, y, z })
    }
    
    for (let i = 0; i < points.length; i++) {
      const p1 = points[i]
      const p2 = points[(i + 1) % points.length]
      positions.push(p1.x, p1.y, p1.z)
      positions.push(p2.x, p2.y, p2.z)
      colors.push(0.2, 0.2, 0.2)
      colors.push(0.2, 0.2, 0.2)
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
    vertexData,
    count: positions.length / 3
  }
}

export { continentColors }
