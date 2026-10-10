/**
 * 高德「Web服务」工具箱：19 项服务的表单定义与调用逻辑
 *
 * 每个工具包含：
 * - fields：表单字段（schema，支持 select/textarea、按 action 联动显示）
 * - run(values)：发起请求并返回归一化结果
 * - kind：结果渲染类型（ServicePanel 按类型渲染）
 */
import { amapRest } from './amapRest.js'
import { toLngLat } from './mapRender.js'

/** 坐标输入通用校验提示 */
const LNG_LAT_HINT = '格式：经度,纬度（小数点后不超过 6 位）'

/** 通用分页字段 */
function pageFields(defaultOffset = 10) {
  return [
    { key: 'page', label: '页码', type: 'text', default: '1', hint: '当前页数' },
    { key: 'offset', label: '每页条数', type: 'text', default: String(defaultOffset), hint: '建议 ≤ 25' }
  ]
}

/** 路径规划策略选项 */
const DRIVING_STRATEGIES = [
  { v: '0', n: '速度优先' },
  { v: '1', n: '费用优先' },
  { v: '2', n: '距离优先' },
  { v: '10', n: '不走高速' },
  { v: '11', n: '高速优先' },
  { v: '12', n: '躲避拥堵' }
]
const TRANSIT_STRATEGIES = [
  { v: '0', n: '推荐模式' },
  { v: '1', n: '最快' },
  { v: '2', n: '最经济' },
  { v: '3', n: '最少换乘' },
  { v: '4', n: '最少步行' }
]

/** 把「米」格式化为可读文本 */
function fmtDist(m) {
  const n = Number(m) || 0
  return n >= 1000 ? `${(n / 1000).toFixed(1)} 公里` : `${Math.round(n)} 米`
}

/** 把「秒」格式化为可读文本 */
function fmtDuration(s) {
  const min = Math.round((Number(s) || 0) / 60)
  if (min < 60) return `${min} 分钟`
  return `${Math.floor(min / 60)} 小时 ${min % 60} 分`
}

/** 把分号串接的 path 字符串序列收集为点位数组（用于地图绘制） */
function collectPath(paths) {
  const pts = []
  for (const seg of paths) {
    for (const p of String(seg || '').split(';')) {
      const ll = toLngLat(p)
      if (ll) pts.push(ll)
    }
  }
  return pts
}

/** v3驾车/步行 path → 统一路线结构 */
function normalizeV3Path(path, modeLabel) {
  const steps = (path.steps || []).map(s => ({
    instruction: s.instruction || '继续前行',
    road: s.road || '',
    distanceText: s.distance ? fmtDist(s.distance) : '',
    location: (s.path || '').split(';').filter(Boolean)[0] || ''
  }))
  const pathLines = (path.steps || []).map(s => s.path)
  return {
    modeLabel,
    distanceText: fmtDist(path.distance),
    durationText: fmtDuration(path.time || path.duration),
    steps,
    path: collectPath(pathLines)
  }
}

/** v3公交 transits[0] → 统一路线结构 */
function normalizeTransit(transit) {
  const steps = []
  const walkPaths = []
  for (const seg of transit.segments || []) {
    const w = seg.walking
    if (w && Number(w.distance) > 0) {
      const first = w.steps?.[0]
      walkPaths.push(...(w.steps || []).map(s => s.path))
      steps.push({
        instruction: `步行 ${fmtDist(w.distance)}`,
        road: first?.instruction || '',
        distanceText: fmtDist(w.distance),
        location: (first?.path || '').split(';').filter(Boolean)[0] || ''
      })
    }
    const line = seg.bus?.buslines?.[0]
    if (line) {
      steps.push({
        instruction: `乘坐 ${line.name}`,
        road: `${line.departure_stop?.name || ''} 上车 → ${line.arrival_stop?.name || ''} 下车`,
        distanceText: line.distance ? fmtDist(line.distance) : '',
        location: line.departure_stop?.location || ''
      })
    }
    if (seg.railway) {
      steps.push({
        instruction: `乘坐 ${seg.railway.name || '城际列车'}`,
        road: `${seg.railway.departure_stop?.name || ''} → ${seg.railway.arrival_stop?.name || ''}`,
        distanceText: seg.railway.distance ? fmtDist(seg.railway.distance) : '',
        location: seg.railway.departure_stop?.location || ''
      })
    }
  }
  return {
    modeLabel: '公交',
    distanceText: transit.distance ? fmtDist(transit.distance) : '',
    durationText: fmtDuration(transit.duration),
    steps,
    path: collectPath(walkPaths)
  }
}

/** v4骑行 path → 统一路线结构 */
function normalizeBicycling(path) {
  const steps = (path.steps || []).map(s => ({
    instruction: s.instruction || s.road || '继续骑行',
    road: s.road || '',
    distanceText: s.distance ? fmtDist(s.distance) : '',
    location: (s.polyline || '').split(';').filter(Boolean)[0] || ''
  }))
  return {
    modeLabel: '骑行',
    distanceText: fmtDist(path.distance),
    durationText: fmtDuration(path.duration),
    steps,
    path: collectPath((path.steps || []).map(s => s.polyline))
  }
}

export const SERVICE_GROUPS = [
  /* ================= 地图服务 ================= */
  {
    id: 'map',
    title: '地图服务',
    tools: [
      {
        id: 'staticmap',
        name: '静态地图',
        icon: '🗺️',
        desc: '根据坐标、缩放级别与标注，直接生成一张地图图片（无需 JSAPI 加载）。',
        kind: 'staticmap',
        fields: [
          { key: 'location', label: '中心点坐标', required: true, default: '116.481488,39.990464', hint: LNG_LAT_HINT },
          { key: 'zoom', label: '缩放级别', type: 'select', default: '12', options: Array.from({ length: 17 }, (_, i) => ({ v: String(i + 1), n: String(i + 1) })) },
          { key: 'size', label: '图片尺寸', default: '400*400', hint: '宽*高，最大 1024*1024' },
          { key: 'scale', label: '清晰度', type: 'select', default: '2', options: [{ v: '1', n: '普通' }, { v: '2', n: '高清' }] },
          { key: 'markers', label: '标注', type: 'textarea', default: 'mid,0xFF0000,A:116.481488,39.990464', hint: '格式：标记样式,颜色,标签:坐标（多组用 | 分隔）', span: 2 },
          { key: 'paths', label: '折线', type: 'textarea', hint: '格式：透明度,颜色,线宽,填充,样式:坐标1;坐标2（可选）', span: 2 }
        ],
        async run(v) {
          const url = amapRest.staticMapUrl(v)
          return { url }
        },
        render(v, res, r) {
          const c = toLngLat(v.location)
          if (c) {
            r.pin(c, '静态地图中心')
            r.fit()
          }
        }
      }
    ]
  },

  /* ================= 位置服务 ================= */
  {
    id: 'location',
    title: '位置服务',
    tools: [
      {
        id: 'geocode',
        name: '地理编码',
        icon: '📍',
        desc: '将结构化地址转换为经纬度坐标，并返回省市区、adcode 等信息。',
        kind: 'geocode',
        fields: [
          { key: 'address', label: '结构化地址', required: true, default: '浙江省杭州市西湖风景名胜区', span: 2 },
          { key: 'city', label: '指定查询城市', hint: '城市名 / citycode / adcode，可选' }
        ],
        async run(v) {
          const d = await amapRest.geocode(v)
          return d.geocodes || []
        },
        render(v, res, r) {
          res.forEach(g => r.dot(g.location, { title: g.formatted_address || '' }))
          r.fit()
        }
      },
      {
        id: 'regeo',
        name: '逆地理编码',
        icon: '🏠',
        desc: '将经纬度坐标转换为详细结构化地址，可附带附近 POI / 道路 / AOI 信息。',
        kind: 'regeo',
        fields: [
          { key: 'location', label: '坐标', required: true, default: '120.153576,30.287459', hint: LNG_LAT_HINT },
          { key: 'radius', label: '搜索半径(米)', default: '1000' },
          { key: 'extensions', label: '返回内容', type: 'select', default: 'all', options: [{ v: 'base', n: '基础地址' }, { v: 'all', n: '含附近POI/道路' }] }
        ],
        async run(v) {
          return amapRest.regeo(v)
        },
        render(v, res, r) {
          r.pin(v.location, res.formatted_address || '解析位置')
          r.circle(v.location, v.radius, { color: r.PALETTE.green })
          r.fit()
        }
      },
      {
        id: 'ip',
        name: 'IP 定位',
        icon: '🌐',
        desc: '根据 IP（默认为请求方 IP）定位到省市级别，返回 adcode 与城市范围。',
        kind: 'ip',
        fields: [
          { key: 'ip', label: 'IP 地址', hint: '留空则定位当前请求方 IP' }
        ],
        async run(v) {
          const d = await amapRest.ip(v)
          const { status, info, infocode, ...rest } = d
          return rest
        },
        render(v, res, r) {
          if (res.rectangle) {
            r.rect(res.rectangle)
            r.fit()
          }
        }
      },
      {
        id: 'convert',
        name: '坐标转换',
        icon: '🔁',
        desc: '将 GPS / 百度 / mapbar 等非高德坐标转换为高德坐标。',
        kind: 'convert',
        fields: [
          { key: 'locations', label: '坐标串', required: true, type: 'textarea', default: '116.481488,39.990464', hint: '多个坐标用 ; 分隔（一次最多 40 对）', span: 2 },
          { key: 'coordsys', label: '源坐标系', type: 'select', default: 'gps', options: [{ v: 'gps', n: 'GPS(WGS84)' }, { v: 'baidu', n: '百度(BD09)' }, { v: 'mapbar', n: '图吧' }, { v: 'autos', n: '自动纠偏' }] }
        ],
        async run(v) {
          const locations = await amapRest.convert(v)
          const list = (locations || '').split(';').filter(Boolean)
          return { source: v.locations, coordsys: v.coordsys, locations, list }
        },
        render(v, res, r) {
          res.list.forEach(c => r.dot(c, { color: r.PALETTE.purple }))
          r.fit()
        }
      }
    ]
  },

  /* ================= 搜索服务 ================= */
  {
    id: 'search',
    title: '搜索服务',
    tools: [
      {
        id: 'place-text',
        name: '关键字搜索',
        icon: '🔍',
        desc: '按关键字与城市搜索 POI（兴趣点），返回名称、地址、坐标、电话等。',
        kind: 'pois',
        fields: [
          { key: 'keywords', label: '关键字', required: true, default: '美食' },
          { key: 'city', label: '城市', default: '杭州' },
          { key: 'citylimit', label: '仅限该城市', type: 'select', default: 'false', options: [{ v: 'true', n: '是' }, { v: 'false', n: '否' }] },
          ...pageFields()
        ],
        async run(v) {
          return amapRest.placeText(v)
        },
        render(v, res, r) {
          res.pois.forEach(p => r.dot(p.location, { title: p.name || '' }))
          r.fit()
        }
      },
      {
        id: 'place-around',
        name: '周边搜索',
        icon: '⭕',
        desc: '以某坐标为圆心搜索周边 POI，可按距离排序。',
        kind: 'pois',
        fields: [
          { key: 'keywords', label: '关键字', default: '地铁站' },
          { key: 'location', label: '中心点坐标', required: true, default: '120.153576,30.287459', hint: LNG_LAT_HINT },
          { key: 'radius', label: '半径(米)', default: '2000', hint: '最大 50000' },
          { key: 'sortrule', label: '排序', type: 'select', default: 'distance', options: [{ v: 'distance', n: '距离优先' }, { v: 'weight', n: '权重优先' }] },
          ...pageFields()
        ],
        async run(v) {
          return amapRest.placeAround(v)
        },
        render(v, res, r) {
          r.circle(v.location, v.radius, { color: r.PALETTE.green })
          res.pois.forEach(p => r.dot(p.location, { color: r.PALETTE.green, title: p.name || '' }))
          r.fit()
        }
      },
      {
        id: 'place-polygon',
        name: '多边形搜索',
        icon: '⬡',
        desc: '在自定义多边形区域内搜索 POI。',
        kind: 'pois',
        fields: [
          { key: 'keywords', label: '关键字', default: '写字楼' },
          { key: 'polygon', label: '多边形顶点', required: true, type: 'textarea', default: '116.471872,39.996076|116.490251,39.996076|116.490251,40.004303|116.471872,40.004303', hint: '坐标用 | 分隔，首尾可不相接（自动闭合）', span: 2 },
          ...pageFields()
        ],
        async run(v) {
          return amapRest.placePolygon(v)
        },
        render(v, res, r) {
          r.pathFromStr(v.polygon, { color: r.PALETTE.purple, fill: true })
          res.pois.forEach(p => r.dot(p.location, { color: r.PALETTE.purple, title: p.name || '' }))
          r.fit()
        }
      },
      {
        id: 'place-detail',
        name: 'ID 查询',
        icon: '🆔',
        desc: '通过 POI ID 查询详情（评分、均价、营业时间、图片等深度信息）。',
        kind: 'json',
        fields: [
          { key: 'id', label: 'POI ID', required: true, placeholder: '如 B0FFG7Z0DR（搜索结果可获取）', span: 2 }
        ],
        async run(v) {
          const poi = await amapRest.placeDetail(v.id)
          if (!poi) throw new Error('未查询到该 POI，请确认 ID 是否正确')
          return poi
        },
        render(v, res, r) {
          if (res.location) {
            r.pin(res.location, res.name || 'POI')
            r.fit()
          }
        }
      },
      {
        id: 'inputtips',
        name: '输入提示',
        icon: '💡',
        desc: '根据输入关键字返回候选词列表，用于搜索框自动补全。',
        kind: 'tips',
        fields: [
          { key: 'keywords', label: '关键字', required: true, default: '西湖' },
          { key: 'city', label: '限定城市', hint: '可选' },
          { key: 'citylimit', label: '仅返回该城市', type: 'select', default: 'false', options: [{ v: 'true', n: '是' }, { v: 'false', n: '否' }] },
          { key: 'location', label: '参考坐标', hint: '可选，用于距离计算', span: 2 }
        ],
        async run(v) {
          const tips = await amapRest.inputtips(v)
          return tips
        },
        render(v, res, r) {
          res.forEach(t => r.dot(t.location, { title: t.name || '' }))
          r.fit()
        }
      }
    ]
  },

  /* ================= 出行服务 ================= */
  {
    id: 'travel',
    title: '出行服务',
    tools: [
      {
        id: 'direction',
        name: '路径规划',
        icon: '🧭',
        desc: '驾车 / 公交 / 步行 / 骑行路线规划，返回距离、耗时与分步引导。',
        kind: 'route',
        fields: [
          { key: 'origin', label: '起点坐标', required: true, default: '120.209932,30.245843', hint: LNG_LAT_HINT + '（默认：杭州东站）' },
          { key: 'destination', label: '终点坐标', required: true, default: '120.130396,30.241567', hint: LNG_LAT_HINT + '（默认：西湖风景名胜区）' },
          { key: 'mode', label: '出行方式', type: 'select', default: 'driving', options: [{ v: 'driving', n: '驾车' }, { v: 'transit', n: '公交' }, { v: 'walking', n: '步行' }, { v: 'bicycling', n: '骑行' }] },
          { key: 'strategy', label: '策略', type: 'select', default: '0', options: DRIVING_STRATEGIES, showIf: v => v.mode === 'driving' || v.mode === 'transit' },
          { key: 'city', label: '起点城市', default: '杭州', showIf: v => v.mode === 'transit' },
          { key: 'cityd', label: '终点城市', hint: '可选', showIf: v => v.mode === 'transit' }
        ],
        async run(v) {
          if (v.mode === 'transit') {
            const transits = await amapRest.transit({
              origin: v.origin, destination: v.destination, city: v.city, cityd: v.cityd, strategy: v.strategy
            })
            return normalizeTransit(transits[0])
          }
          if (v.mode === 'walking') {
            return normalizeV3Path(await amapRest.walking({ origin: v.origin, destination: v.destination }), '步行')
          }
          if (v.mode === 'bicycling') {
            return normalizeBicycling(await amapRest.bicycling({ origin: v.origin, destination: v.destination }))
          }
          return normalizeV3Path(
            await amapRest.driving({ origin: v.origin, destination: v.destination, strategy: v.strategy || '0' }),
            '驾车'
          )
        },
        render(v, res, r) {
          const o = toLngLat(v.origin)
          const d = toLngLat(v.destination)
          if (o) r.pin(o, '起点')
          if (d) r.pin(d, '终点')
          if (res.path?.length > 1) {
            r.polyline(res.path, { color: r.PALETTE.blue, width: 5 })
          } else if (o && d) {
            r.polyline([o, d], { color: r.PALETTE.blue, width: 3, dashed: true })
          }
          r.fit()
        }
      }
    ]
  },

  /* ================= 气象服务 ================= */
  {
    id: 'weather-group',
    title: '气象服务',
    tools: [
      {
        id: 'weather',
        name: '天气查询',
        icon: '🌤️',
        desc: '查询实况天气（base）与未来 4 天预报（all），city 需传区县级 adcode。',
        kind: 'weather',
        fields: [
          { key: 'city', label: '城市 adcode', required: true, default: '330100', hint: '如 330100=杭州市（可在「行政区划查询」中获取）' },
          { key: 'extensions', label: '查询类型', type: 'select', default: 'all', options: [{ v: 'base', n: '实况天气' }, { v: 'all', n: '预报天气' }] }
        ],
        async run(v) {
          return amapRest.weather(v)
        }
      }
    ]
  },

  /* ================= 交通态势 ================= */
  {
    id: 'traffic',
    title: '交通态势',
    tools: [
      {
        id: 'traffic-rectangle',
        name: '矩形区域路况',
        icon: '▭',
        desc: '查询矩形范围内的交通态势（畅通/缓行/拥堵评价 + 路况详情）。',
        kind: 'traffic',
        fields: [
          { key: 'rectangle', label: '矩形范围', required: true, type: 'textarea', default: '120.072958,30.226334;120.242035,30.340858', hint: '格式：左下角坐标;右上角坐标（对角线点）', span: 2 },
          { key: 'level', label: '路况精度', type: 'select', default: '4', options: [{ v: '1', n: '一级（主干道）' }, { v: '2', n: '二级（含次干道）' }, { v: '3', n: '三级（含支路）' }, { v: '4', n: '四级（最细）' }] },
          { key: 'extensions', label: '返回内容', type: 'select', default: 'all', options: [{ v: 'base', n: '整体评价' }, { v: 'all', n: '含具体路段' }] }
        ],
        async run(v) {
          return amapRest.trafficRectangle(v)
        },
        render(v, res, r) {
          r.rect(v.rectangle)
          r.fit()
        }
      },
      {
        id: 'traffic-circle',
        name: '圆形区域路况',
        icon: '⭕',
        desc: '查询以坐标为圆心、指定半径范围内的交通态势。',
        kind: 'traffic',
        fields: [
          { key: 'location', label: '圆心坐标', required: true, default: '120.153576,30.287459', hint: LNG_LAT_HINT },
          { key: 'radius', label: '半径(米)', default: '3000', hint: '最大 50000' },
          { key: 'level', label: '路况精度', type: 'select', default: '4', options: [{ v: '1', n: '一级' }, { v: '2', n: '二级' }, { v: '3', n: '三级' }, { v: '4', n: '四级' }] },
          { key: 'extensions', label: '返回内容', type: 'select', default: 'all', options: [{ v: 'base', n: '整体评价' }, { v: 'all', n: '含具体路段' }] }
        ],
        async run(v) {
          return amapRest.trafficCircle(v)
        },
        render(v, res, r) {
          r.circle(v.location, v.radius, { color: r.PALETTE.orange })
          r.fit()
        }
      },
      {
        id: 'traffic-road',
        name: '指定线路路况',
        icon: '🛣️',
        desc: '查询指定道路名称的路况与拥堵路段。',
        kind: 'traffic',
        fields: [
          { key: 'name', label: '道路名称', required: true, default: '中河高架', span: 2 },
          { key: 'adcode', label: '所在城市 adcode', default: '330100', hint: '可填城市或区县 adcode' },
          { key: 'level', label: '路况精度', type: 'select', default: '4', options: [{ v: '1', n: '一级' }, { v: '2', n: '二级' }, { v: '3', n: '三级' }, { v: '4', n: '四级' }] }
        ],
        async run(v) {
          return amapRest.trafficRoad(v)
        }
      }
    ]
  },

  /* ================= 行政区划 ================= */
  {
    id: 'admin',
    title: '行政区划',
    tools: [
      {
        id: 'district',
        name: '行政区划查询',
        icon: '🏛️',
        desc: '按名称/adcode 查询行政区划，支持逐级下钻（省 → 市 → 区县 → 街道）。',
        kind: 'district',
        fields: [
          { key: 'keywords', label: '关键词', default: '浙江省', hint: '行政区名称 / citycode / adcode（留空则返回全部省级）' },
          { key: 'subdistrict', label: '下钻层级', type: 'select', default: '2', options: [{ v: '0', n: '不返回子级' }, { v: '1', n: '一级' }, { v: '2', n: '二级' }, { v: '3', n: '三级' }] },
          { key: 'extensions', label: '返回内容', type: 'select', default: 'base', options: [{ v: 'base', n: '基础' }, { v: 'all', n: '含边界坐标' }] },
          { key: 'filter', label: '过滤条件', hint: '如 adcode=110000，可选' }
        ],
        async run(v) {
          const districts = await amapRest.district(v)
          if (!districts.length) throw new Error('未查询到行政区划，请调整关键词')
          return districts
        },
        render(v, res, r) {
          if (v.extensions !== 'all') return
          const boundaries = []
          const walk = list => (list || []).forEach(d => {
            if (d.polyline) boundaries.push(d.polyline)
            if (d.districts?.length) walk(d.districts)
          })
          walk(res)
          boundaries.slice(0, 40).forEach(pl => r.pathFromStr(pl, { color: r.PALETTE.blue, width: 2 }))
          r.fit()
        }
      }
    ]
  },

  /* ================= 平台级服务 ================= */
  {
    id: 'platform',
    title: '平台级服务',
    tools: [
      {
        id: 'geofence',
        name: '地理围栏',
        icon: '🚧',
        desc: '创建圆形/多边形围栏、查询围栏列表、查询设备进出状态。需在高德控制台为该 Key 开通「地理围栏」服务。',
        kind: 'json',
        fields: [
          {
            key: 'action', label: '操作', type: 'select', default: 'list',
            options: [{ v: 'create', n: '创建围栏' }, { v: 'list', n: '查询围栏' }, { v: 'status', n: '设备状态查询' }]
          },
          { key: 'name', label: '围栏名称', default: '办公区围栏', showIf: v => v.action === 'create' },
          { key: 'shape', label: '围栏形状', type: 'select', default: 'circle', options: [{ v: 'circle', n: '圆形' }, { v: 'polygon', n: '多边形' }], showIf: v => v.action === 'create' },
          { key: 'center', label: '圆心坐标', default: '120.153576,30.287459', hint: LNG_LAT_HINT, showIf: v => v.action === 'create' && v.shape === 'circle' },
          { key: 'radius', label: '半径(米)', default: '1000', hint: '50 ~ 10000', showIf: v => v.action === 'create' && v.shape === 'circle' },
          { key: 'points', label: '顶点坐标串', type: 'textarea', default: '120.148,30.290|120.158,30.290|120.158,30.282|120.148,30.282', hint: '多边形顶点，| 分隔（3~100 个）', showIf: v => v.action === 'create' && v.shape === 'polygon', span: 2 },
          { key: 'sid', label: '围栏 ID (sid)', required: true, placeholder: '创建成功后返回', showIf: v => v.action === 'status' },
          { key: 'loc', label: '设备坐标', required: true, default: '120.153576,30.287459', hint: LNG_LAT_HINT, showIf: v => v.action === 'status' },
          { key: 'sid_q', label: '按 sid 查询', hint: '可选', showIf: v => v.action === 'list' }
        ],
        async run(v) {
          if (v.action === 'create') {
            const params = { name: v.name, shape: v.shape }
            if (v.shape === 'circle') {
              params.center = v.center
              params.radius = v.radius
            } else {
              params.points = v.points
            }
            const data = await amapRest.fenceCreate(params)
            return { message: '围栏创建成功', data }
          }
          if (v.action === 'status') {
            const data = await amapRest.fenceStatus({ sid: v.sid, loc: v.loc })
            return { sid: v.sid, loc: v.loc, data }
          }
          const data = await amapRest.fenceList({ sid: v.sid_q })
          return { message: '围栏查询成功', data }
        },
        render(v, res, r) {
          if (v.action === 'create') {
            if (v.shape === 'circle') {
              r.circle(v.center, v.radius, { color: r.PALETTE.red })
            } else {
              r.pathFromStr(v.points, { color: r.PALETTE.red, fill: true })
            }
            r.fit()
          } else if (v.action === 'status') {
            r.pin(v.loc, '设备位置')
            r.fit()
          } else {
            const list = res?.data?.list || res?.data?.fences || []
            if (Array.isArray(list)) {
              list.forEach(f => {
                if (f?.shape === 'circle' && f.center) {
                  r.circle(f.center, f.radius, { color: r.PALETTE.red })
                } else if (f?.points) {
                  r.pathFromStr(f.points, { color: r.PALETTE.red, fill: true })
                }
              })
              r.fit()
            }
          }
        }
      },
      {
        id: 'track',
        name: '猎鹰服务',
        icon: '🛰️',
        desc: '高德猎鹰轨迹服务：管理服务/终端、查询历史轨迹。需在高德控制台为该 Key 开通「猎鹰轨迹服务」。',
        kind: 'json',
        fields: [
          {
            key: 'action', label: '操作', type: 'select', default: 'serviceList',
            options: [
              { v: 'serviceList', n: '查询服务列表' },
              { v: 'serviceCreate', n: '创建服务' },
              { v: 'terminalList', n: '查询终端列表' },
              { v: 'trackSearch', n: '查询终端轨迹' }
            ]
          },
          { key: 'name', label: '服务名称', required: true, placeholder: '如：配送车队', showIf: v => v.action === 'serviceCreate' },
          { key: 'sid', label: '服务 ID (sid)', required: true, showIf: v => v.action === 'terminalList' || v.action === 'trackSearch' },
          { key: 'tid', label: '终端 ID (tid)', required: true, showIf: v => v.action === 'trackSearch' },
          { key: 'start_time', label: '起始时间(ms)', placeholder: 'Unix 毫秒时间戳，可选', showIf: v => v.action === 'trackSearch' },
          { key: 'end_time', label: '结束时间(ms)', placeholder: 'Unix 毫秒时间戳，可选', showIf: v => v.action === 'trackSearch' }
        ],
        async run(v) {
          if (v.action === 'serviceCreate') {
            return { message: '服务创建成功', data: await amapRest.trackServiceCreate(v.name) }
          }
          if (v.action === 'terminalList') {
            return { message: `服务 ${v.sid} 的终端列表`, data: await amapRest.trackTerminalList({ sid: v.sid, page: 1, size: 20 }) }
          }
          if (v.action === 'trackSearch') {
            return {
              message: `终端 ${v.tid} 的轨迹`,
              data: await amapRest.trackSearch({
                sid: v.sid, tid: v.tid,
                start_time: v.start_time, end_time: v.end_time,
                is_processing: '1', page: 1, size: 100
              })
            }
          }
          return { message: '服务列表', data: await amapRest.trackServiceList() }
        }
      },
      {
        id: 'geohub',
        name: 'GeoHUB 服务',
        icon: '📦',
        desc: 'GeoHUB 行业数据服务转发调用：输入完整接口地址与附加参数，自动携带 Key 发起请求。数据产品需在高德控制台单独订购。',
        kind: 'json',
        fields: [
          { key: 'url', label: '接口地址', required: true, type: 'textarea', placeholder: 'https://... 或 restapi 路径（自动携带 Key）', span: 2 },
          { key: 'params', label: '附加参数', type: 'textarea', hint: '每行一个，格式：参数名=参数值', span: 2 }
        ],
        async run(v) {
          const extra = {}
          for (const line of (v.params || '').split('\n')) {
            const i = line.indexOf('=')
            if (i > 0) extra[line.slice(0, i).trim()] = line.slice(i + 1).trim()
          }
          const data = await amapRest.geohub(v.url, extra)
          return { url: v.url, data }
        }
      }
    ]
  }
]

/** 扁平化 19 项工具列表（供计数/检索） */
export const ALL_TOOLS = SERVICE_GROUPS.flatMap(g => g.tools)

/** 根据字段 schema 生成表单初始值 */
export function initialValues(tool) {
  const values = {}
  for (const f of tool.fields) {
    values[f.key] = f.default ?? ''
  }
  return values
}
