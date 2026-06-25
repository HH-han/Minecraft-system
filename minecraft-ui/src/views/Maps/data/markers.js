/**
 * Glow marker data for continents and representative countries
 * Each marker floats above the geographic center of its region
 */
export const glowMarkers = [
  // === Continents ===
  {
    id: 'asia',
    type: 'continent',
    name: '亚洲',
    nameEn: 'Asia',
    lat: 35,
    lng: 100,
    color: [0.9, 0.6, 0.3],
    size: 1.2
  },
  {
    id: 'europe',
    type: 'continent',
    name: '欧洲',
    nameEn: 'Europe',
    lat: 54,
    lng: 15,
    color: [0.3, 0.6, 0.9],
    size: 1.1
  },
  {
    id: 'africa',
    type: 'continent',
    name: '非洲',
    nameEn: 'Africa',
    lat: 5,
    lng: 20,
    color: [0.9, 0.7, 0.2],
    size: 1.2
  },
  {
    id: 'north-america',
    type: 'continent',
    name: '北美洲',
    nameEn: 'North America',
    lat: 45,
    lng: -100,
    color: [0.4, 0.7, 0.4],
    size: 1.2
  },
  {
    id: 'south-america',
    type: 'continent',
    name: '南美洲',
    nameEn: 'South America',
    lat: -15,
    lng: -60,
    color: [0.2, 0.8, 0.5],
    size: 1.1
  },
  {
    id: 'oceania',
    type: 'continent',
    name: '大洋洲',
    nameEn: 'Oceania',
    lat: -25,
    lng: 135,
    color: [0.5, 0.8, 0.9],
    size: 1.0
  },
  {
    id: 'antarctica',
    type: 'continent',
    name: '南极洲',
    nameEn: 'Antarctica',
    lat: -80,
    lng: 0,
    color: [0.7, 0.9, 1.0],
    size: 1.0
  },
  // === Representative Countries ===
  {
    id: 'cn',
    type: 'country',
    name: '中国',
    nameEn: 'China',
    lat: 35.8617,
    lng: 104.1954,
    continentId: 'asia',
    color: [0.95, 0.55, 0.35],
    size: 0.9
  },
  {
    id: 'jp',
    type: 'country',
    name: '日本',
    nameEn: 'Japan',
    lat: 36.2048,
    lng: 138.2529,
    continentId: 'asia',
    color: [0.95, 0.7, 0.5],
    size: 0.8
  },
  {
    id: 'in',
    type: 'country',
    name: '印度',
    nameEn: 'India',
    lat: 20.5937,
    lng: 78.9629,
    continentId: 'asia',
    color: [0.9, 0.65, 0.3],
    size: 0.85
  },
  {
    id: 'fr',
    type: 'country',
    name: '法国',
    nameEn: 'France',
    lat: 46.2276,
    lng: 2.2137,
    continentId: 'europe',
    color: [0.4, 0.6, 0.95],
    size: 0.8
  },
  {
    id: 'de',
    type: 'country',
    name: '德国',
    nameEn: 'Germany',
    lat: 51.1657,
    lng: 10.4515,
    continentId: 'europe',
    color: [0.5, 0.7, 0.9],
    size: 0.8
  },
  {
    id: 'gb',
    type: 'country',
    name: '英国',
    nameEn: 'United Kingdom',
    lat: 55.3781,
    lng: -3.436,
    continentId: 'europe',
    color: [0.35, 0.55, 0.85],
    size: 0.75
  },
  {
    id: 'us',
    type: 'country',
    name: '美国',
    nameEn: 'United States',
    lat: 37.0902,
    lng: -95.7129,
    continentId: 'north-america',
    color: [0.5, 0.8, 0.5],
    size: 0.9
  },
  {
    id: 'ca',
    type: 'country',
    name: '加拿大',
    nameEn: 'Canada',
    lat: 56.1304,
    lng: -106.3468,
    continentId: 'north-america',
    color: [0.4, 0.7, 0.6],
    size: 0.8
  },
  {
    id: 'br',
    type: 'country',
    name: '巴西',
    nameEn: 'Brazil',
    lat: -14.235,
    lng: -51.9253,
    continentId: 'south-america',
    color: [0.3, 0.75, 0.45],
    size: 0.85
  },
  {
    id: 'ar',
    type: 'country',
    name: '阿根廷',
    nameEn: 'Argentina',
    lat: -38.4161,
    lng: -63.6167,
    continentId: 'south-america',
    color: [0.6, 0.8, 0.7],
    size: 0.75
  },
  {
    id: 'eg',
    type: 'country',
    name: '埃及',
    nameEn: 'Egypt',
    lat: 26.8206,
    lng: 30.8025,
    continentId: 'africa',
    color: [0.85, 0.7, 0.3],
    size: 0.8
  },
  {
    id: 'za',
    type: 'country',
    name: '南非',
    nameEn: 'South Africa',
    lat: -30.5595,
    lng: 22.9375,
    continentId: 'africa',
    color: [0.8, 0.6, 0.4],
    size: 0.75
  },
  {
    id: 'au',
    type: 'country',
    name: '澳大利亚',
    nameEn: 'Australia',
    lat: -25.2744,
    lng: 133.7751,
    continentId: 'oceania',
    color: [0.6, 0.85, 0.8],
    size: 0.8
  }
]

export function getMarkerById(id) {
  return glowMarkers.find(m => m.id === id)
}

export function getMarkersByType(type) {
  return glowMarkers.filter(m => m.type === type)
}

export function getMarkersByContinent(continentId) {
  return glowMarkers.filter(m => m.continentId === continentId)
}