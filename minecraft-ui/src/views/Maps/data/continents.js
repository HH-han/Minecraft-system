export const continents = [
  {
    id: 'asia',
    name: '亚洲',
    nameEn: 'Asia',
    area: '4457.9万平方公里',
    countries: 48,
    population: '约45亿',
    description: '亚洲是世界上面积最大、人口最多的大洲，拥有悠久的历史和灿烂的文化。',
    centerLat: 35,
    centerLng: 100,
    color: [0.9, 0.6, 0.3, 0.8],
    highlights: [
      { name: '中国', lat: 35.8617, lng: 104.1954 },
      { name: '日本', lat: 36.2048, lng: 138.2529 },
      { name: '印度', lat: 20.5937, lng: 78.9629 }
    ]
  },
  {
    id: 'europe',
    name: '欧洲',
    nameEn: 'Europe',
    area: '1016万平方公里',
    countries: 44,
    population: '约7.4亿',
    description: '欧洲是世界上发达国家最集中的大洲，拥有丰富的历史文化遗产。',
    centerLat: 54,
    centerLng: 15,
    color: [0.3, 0.6, 0.9, 0.8],
    highlights: [
      { name: '法国', lat: 46.2276, lng: 2.2137 },
      { name: '德国', lat: 51.1657, lng: 10.4515 },
      { name: '英国', lat: 55.3781, lng: -3.436 }
    ]
  },
  {
    id: 'africa',
    name: '非洲',
    nameEn: 'Africa',
    area: '3022万平方公里',
    countries: 54,
    population: '约12亿',
    description: '非洲是世界第二大洲，拥有丰富的自然资源和多样的野生动物。',
    centerLat: 5,
    centerLng: 20,
    color: [0.9, 0.7, 0.2, 0.8],
    highlights: [
      { name: '埃及', lat: 26.8206, lng: 30.8025 },
      { name: '南非', lat: -30.5595, lng: 22.9375 },
      { name: '肯尼亚', lat: -0.0236, lng: 37.9062 }
    ]
  },
  {
    id: 'north-america',
    name: '北美洲',
    nameEn: 'North America',
    area: '2422.8万平方公里',
    countries: 23,
    population: '约5.6亿',
    description: '北美洲是世界第三大洲，经济高度发达，自然景观多样。',
    centerLat: 45,
    centerLng: -100,
    color: [0.4, 0.7, 0.4, 0.8],
    highlights: [
      { name: '美国', lat: 37.0902, lng: -95.7129 },
      { name: '加拿大', lat: 56.1304, lng: -106.3468 },
      { name: '墨西哥', lat: 23.6345, lng: -102.5528 }
    ]
  },
  {
    id: 'south-america',
    name: '南美洲',
    nameEn: 'South America',
    area: '1784万平方公里',
    countries: 12,
    population: '约4亿',
    description: '南美洲拥有世界最大的热带雨林和最长的山脉，自然景观壮丽。',
    centerLat: -15,
    centerLng: -60,
    color: [0.2, 0.8, 0.5, 0.8],
    highlights: [
      { name: '巴西', lat: -14.235, lng: -51.9253 },
      { name: '阿根廷', lat: -38.4161, lng: -63.6167 },
      { name: '秘鲁', lat: -9.19, lng: -75.0152 }
    ]
  },
  {
    id: 'oceania',
    name: '大洋洲',
    nameEn: 'Oceania',
    area: '897万平方公里',
    countries: 14,
    population: '约3500万',
    description: '大洋洲是世界上最小的大洲，由众多岛屿组成，拥有独特的生态系统。',
    centerLat: -25,
    centerLng: 135,
    color: [0.5, 0.8, 0.9, 0.8],
    highlights: [
      { name: '澳大利亚', lat: -25.2744, lng: 133.7751 },
      { name: '新西兰', lat: -40.9006, lng: 174.886 },
      { name: '斐济', lat: -17.7134, lng: 178.065 }
    ]
  },
  {
    id: 'antarctica',
    name: '南极洲',
    nameEn: 'Antarctica',
    area: '1424万平方公里',
    countries: 0,
    population: '约1000人（科考）',
    description: '南极洲是地球上最寒冷、最干燥的大陆，被厚厚的冰盖覆盖。',
    centerLat: -80,
    centerLng: 0,
    color: [0.7, 0.9, 1.0, 0.8],
    highlights: [
      { name: '南极点', lat: -90, lng: 0 },
      { name: '麦克默多站', lat: -77.8463, lng: 166.6682 },
      { name: '长城站', lat: -62.1819, lng: -58.9747 }
    ]
  }
]

export function getContinentById(id) {
  return continents.find(c => c.id === id)
}

export function findNearestContinent(lat, lng) {
  let nearest = null
  let minDist = Infinity
  for (const cont of continents) {
    const d = haversineDistance(lat, lng, cont.centerLat, cont.centerLng)
    if (d < minDist) {
      minDist = d
      nearest = cont
    }
  }
  return nearest
}

function haversineDistance(lat1, lng1, lat2, lng2) {
  const R = 6371
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
    Math.sin(dLng / 2) ** 2
  return 2 * R * Math.asin(Math.sqrt(a))
}
