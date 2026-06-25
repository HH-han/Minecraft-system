import { getContinentPolygons, getCountryFeatures, geoPolygonToTriangleMesh, geoPolygonToLineSegments } from './geoBoundaries.js'

const continentColors = {
  asia: { main: [0.95, 0.55, 0.35], name: '亚洲' },
  europe: { main: [0.35, 0.65, 0.95], name: '欧洲' },
  africa: { main: [0.85, 0.65, 0.25], name: '非洲' },
  'north-america': { main: [0.45, 0.75, 0.55], name: '北美洲' },
  'south-america': { main: [0.75, 0.45, 0.65], name: '南美洲' },
  oceania: { main: [0.55, 0.85, 0.75], name: '大洋洲' },
  antarctica: { main: [0.85, 0.85, 0.85], name: '南极洲' }
}

/**
 * Map world-atlas continent names to our internal continent keys
 */
const continentNameToKey = {
  'Asia': 'asia',
  'Europe': 'europe',
  'Africa': 'africa',
  'North America': 'north-america',
  'South America': 'south-america',
  'Oceania': 'oceania',
  'Antarctica': 'antarctica'
}

/**
 * Generate continent triangle mesh data using real geographic boundaries
 */
export function generateContinentGlobeData(radius = 1, _continents = []) {
  const continentPolygons = getContinentPolygons()
  const allPositions = []
  const allColors = []
  const allIndices = []
  let indexOffset = 0

  for (const [continentName, polygon] of Object.entries(continentPolygons)) {
    const key = continentNameToKey[continentName] || continentName.toLowerCase().replace(/\s+/g, '-')
    const baseColor = continentColors[key]?.main || [0.7, 0.7, 0.7]
    const mesh = geoPolygonToTriangleMesh(polygon, radius, baseColor)

    if (mesh.positions.length === 0) continue

    const vertexCount = mesh.positions.length / 3

    // Add vertices
    for (let i = 0; i < vertexCount; i++) {
      allPositions.push(mesh.positions[i * 3], mesh.positions[i * 3 + 1], mesh.positions[i * 3 + 2])
      allColors.push(mesh.colors[i * 3], mesh.colors[i * 3 + 1], mesh.colors[i * 3 + 2])
    }

    // Generate fan triangles from centroid
    for (let i = 1; i < vertexCount; i++) {
      const next = i === vertexCount - 1 ? 1 : i + 1
      allIndices.push(indexOffset, indexOffset + i, indexOffset + next)
    }
    indexOffset += vertexCount
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

/**
 * Generate country triangle mesh data using real geographic boundaries
 */
export function generateCountryGlobeData(radius = 1) {
  const countries = getCountryFeatures()
  const allPositions = []
  const allColors = []
  const allIndices = []
  let indexOffset = 0

  for (const feat of countries) {
    const continent = feat.properties.continent
    const key = continentNameToKey[continent] || continent?.toLowerCase().replace(/\s+/g, '-') || 'asia'
    const baseColor = continentColors[key]?.main || [0.7, 0.7, 0.7]

    const mesh = geoPolygonToTriangleMesh(feat.geometry, radius, baseColor)
    if (mesh.positions.length === 0) continue

    const vertexCount = mesh.positions.length / 3

    for (let i = 0; i < vertexCount; i++) {
      allPositions.push(mesh.positions[i * 3], mesh.positions[i * 3 + 1], mesh.positions[i * 3 + 2])
      allColors.push(mesh.colors[i * 3], mesh.colors[i * 3 + 1], mesh.colors[i * 3 + 2])
    }

    for (let i = 1; i < vertexCount; i++) {
      const next = i === vertexCount - 1 ? 1 : i + 1
      allIndices.push(indexOffset, indexOffset + i, indexOffset + next)
    }
    indexOffset += vertexCount
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

/**
 * Generate boundary line data using real geographic boundaries
 */
export function generateBoundaryLinesData(radius = 1, _regions = []) {
  const positions = []
  const colors = []

  // Generate continent boundaries
  const continentPolygons = getContinentPolygons()
  for (const polygon of Object.values(continentPolygons)) {
    const segments = geoPolygonToLineSegments(polygon, radius * 1.001)
    for (let i = 0; i < segments.length; i += 3) {
      positions.push(segments[i], segments[i + 1], segments[i + 2])
      colors.push(0.2, 0.2, 0.2)
    }
  }

  // Also add individual country boundaries for more detail
  const countries = getCountryFeatures()
  for (const feat of countries) {
    if (!feat.geometry) continue
    const segments = geoPolygonToLineSegments(feat.geometry, radius * 1.002)
    for (let i = 0; i < segments.length; i += 3) {
      positions.push(segments[i], segments[i + 1], segments[i + 2])
      colors.push(0.15, 0.15, 0.15)
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