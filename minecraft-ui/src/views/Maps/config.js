/**
 * 3D 地球模块全局配置
 *
 * 高德开放平台双 Key 架构（申请地址：https://console.amap.com/dev/key/app）：
 * 1. AMAP_KEY      —— 「Web端(JS API)」类型：负责地图渲染/插件（AMapLoader、覆盖物、路线绘制）
 * 2. AMAP_WEB_KEY  —— 「Web服务」类型：负责 REST 服务（地理编码、搜索、路径规划、天气、交通态势等）
 *
 * 注意：key 类型创建后不可更改，两套 Key 不可混用：
 * - 「Web服务」key 用于 JS API 时被拒绝（USERKEY_PLAT_NOMATCH / infocode 10009）
 * - 「Web端(JS API)」key 调用 restapi.amap.com 的部分服务同样会被拒绝
 */
export const AMAP_KEY = '9c338dc5412a964aa3748b6280244b71'

/**
 * 高德「Web服务」Key：用于 services/amapRest.js 的全部 REST 服务调用。
 * 「Web服务」key 不需要 securityJsCode（安全密钥仅 JSAPI 鉴权使用）。
 * 地理围栏 / 猎鹰 / GeoHUB 需在高德控制台为该 Key 单独开通对应服务。
 */
export const AMAP_WEB_KEY = 'db60e30ce92c69d34e324037b8f79ba7'

/**
 * 安全密钥（securityJsCode）：2021-12-02 之后申请的 key 必须配备安全密钥，
 * 需在 JSAPI 加载前挂载到 window._AMapSecurityConfig。
 * 本模块在 AMapLoader.load() 之前被 import，顶层赋值即可保证生效顺序。
 */
export const AMAP_SECURITY_CODE = '39ad5aeadc0ad0f49b2e013939060f20'
window._AMapSecurityConfig = { securityJsCode: AMAP_SECURITY_CODE }

/**
 * 使用 JSAPI 1.4.15：需 Key + 安全密钥（securityJsCode）完成鉴权，
 * 该版本提供完整的路线规划（驾车/步行/公交）、POI 搜索、地理编码、定位能力。
 */
export const AMAP_VERSION = '1.4.15'

/** 需要预加载的高德插件 */
export const AMAP_PLUGINS = [
  'AMap.ToolBar',
  'AMap.Scale',
  'AMap.Driving',
  'AMap.Walking',
  'AMap.Transfer',
  'AMap.Autocomplete',
  'AMap.Geocoder',
  'AMap.Geolocation'
]

/** 3D 地球视角预设（altitude 单位为地球半径倍数） */
export const VIEW_PRESETS = [
  { id: 'world', name: '全球', lat: 20, lng: 0, alt: 2.6 },
  { id: 'asia', name: '亚洲', lat: 35, lng: 100, alt: 1.7 },
  { id: 'europe', name: '欧洲', lat: 52, lng: 15, alt: 1.8 },
  { id: 'africa', name: '非洲', lat: 5, lng: 20, alt: 1.9 },
  { id: 'north-america', name: '北美', lat: 45, lng: -100, alt: 1.8 },
  { id: 'south-america', name: '南美', lat: -15, lng: -60, alt: 1.8 },
  { id: 'oceania', name: '大洋洲', lat: -25, lng: 140, alt: 1.9 }
]

/** 基础图层默认开关状态 */
export const DEFAULT_LAYERS = {
  // —— 基础 ——
  terrain: false,   // 真实地形起伏（bump 地形）
  contour: false,   // 等高线图层
  clouds: false,    // 动态云层
  heatmap: false,   // 全球人口密度热力
  labels: false,    // 国家标注
  atmosphere: false, // 大气层光晕
  // —— 自然地理（默认关闭，按需开启） ——
  bathymetry: false,   // 海洋深度/海底地形
  water: false,        // 河流/湖泊
  ice: false,          // 冰川/积雪覆盖
  ndvi: false,         // 植被指数 NDVI
  landcover: false,    // 土地覆盖/地表类型
  currents: false,     // 洋流
  wind: false,         // 风场
  precipitation: false, // 降水带
  earthquakes: false,  // 地震带/火山分布
  aurora: false,       // 极光带
  // —— 人文经济 ——
  nightlights: false,  // 夜光（城市灯光）
  flights: false,      // 航线/航路
  cables: false,       // 海底光缆
  borders: false,      // 国界示意线
  cities: false,       // 城市点位/首都
  timezones: false,    // 时区线
  culture: false,      // 语言/文化圈分布
  gdp: false,          // GDP/经济密度热力
  // —— 环境动态 ——
  airquality: false,   // 空气质量（PM2.5）
  co2: false,          // CO₂/碳排放
  sealevel: false,     // 海平面上升模拟
  typhoons: false,     // 台风/气旋路径
  wildfires: false,    // 野火热点
  ships: false,        // 船舶/AIS 航迹
  satellites: false,   // 卫星轨道/星座
  daynight: false,     // 实时昼夜分界线
  // —— 渲染氛围 ——
  stars: false,        // 星空背景
  sunmoon: false,      // 太阳/月球天体
  bloom: false,        // 泛光/亮度增强
  fog: false,          // 景深雾/暗角
  graticule: false,    // 经纬网格
  compass: false,      // 指北针/比例尺
  hillshade: false     // 地形阴影
}

/**
 * 图层分组定义（ControlPanel 渲染用）
 * key 需与 DEFAULT_LAYERS / 图层实现模块一一对应
 */
export const LAYER_GROUPS = [
  {
    id: 'base', icon: '🧩', title: '基础图层', open: true,
    layers: [
      { key: 'terrain', name: '地形起伏', desc: '真实高程凹凸贴图', icon: '⛰️' },
      { key: 'contour', name: '等高线', desc: '高程等值线可视化', icon: '🧭' },
      { key: 'clouds', name: '动态云层', desc: '自转云层动画', icon: '☁️' },
      { key: 'heatmap', name: '人口热力', desc: '全球人口密度', icon: '🔥' },
      { key: 'labels', name: '国家标注', desc: '点击查看详情', icon: '🏷️' },
      { key: 'atmosphere', name: '大气层', desc: '大气光晕效果', icon: '🌌' }
    ]
  },
  {
    id: 'nature', icon: '🗺️', title: '自然地理', open: false,
    layers: [
      { key: 'bathymetry', name: '海底地形', desc: '海沟/洋中脊深度渲染', icon: '🌊' },
      { key: 'water', name: '河流湖泊', desc: '主要水系水体', icon: '💧' },
      { key: 'ice', name: '冰川积雪', desc: '极地冰盖季节变化', icon: '🧊' },
      { key: 'ndvi', name: '植被指数', desc: '全球绿度 NDVI', icon: '🌿' },
      { key: 'landcover', name: '土地覆盖', desc: '森林/荒漠/冰原分类', icon: '🏞️' },
      { key: 'currents', name: '洋流', desc: '主要洋流动态流线', icon: '🔁' },
      { key: 'wind', name: '风场', desc: '信风/西风带粒子', icon: '🍃' },
      { key: 'precipitation', name: '降水带', desc: '雨带动态粒子', icon: '🌧️' },
      { key: 'earthquakes', name: '地震火山', desc: '环太平洋火山带', icon: '🌋' },
      { key: 'aurora', name: '极光带', desc: '高纬度动态光幕', icon: '✨' }
    ]
  },
  {
    id: 'human', icon: '🏙️', title: '人文经济', open: false,
    layers: [
      { key: 'nightlights', name: '夜光图层', desc: '城市灯光强度', icon: '💡' },
      { key: 'flights', name: '航空航线', desc: '主要枢纽动态航线', icon: '✈️' },
      { key: 'cables', name: '海底光缆', desc: '全球互联网干线', icon: '🔌' },
      { key: 'borders', name: '国界示意', desc: '国家范围示意线', icon: '🔲' },
      { key: 'cities', name: '城市点位', desc: '人口规模圆点+名称', icon: '📍' },
      { key: 'timezones', name: '时区线', desc: '24 时区经线', icon: '🕐' },
      { key: 'culture', name: '语言文化圈', desc: '语系分布示意', icon: '🗣️' },
      { key: 'gdp', name: '经济热力', desc: 'GDP 经济密度', icon: '💰' }
    ]
  },
  {
    id: 'dynamic', icon: '🌪️', title: '环境动态', open: false,
    layers: [
      { key: 'airquality', name: '空气质量', desc: 'PM2.5 热点分布', icon: '😷' },
      { key: 'co2', name: '碳排放', desc: '排放热点上升粒子', icon: '🏭' },
      { key: 'sealevel', name: '海平面上升', desc: '淹没范围模拟', icon: '📈' },
      { key: 'typhoons', name: '台风路径', desc: '典型气旋轨迹', icon: '🌀' },
      { key: 'wildfires', name: '野火热点', desc: '全球火点闪烁', icon: '🔥' },
      { key: 'ships', name: '船舶航迹', desc: '海运 AIS 流线', icon: '🚢' },
      { key: 'satellites', name: '卫星轨道', desc: '星座/静止轨道', icon: '🛰️' },
      { key: 'daynight', name: '昼夜分界', desc: '实时晨昏线', icon: '🌗' }
    ]
  },
  {
    id: 'ambient', icon: '🎨', title: '渲染氛围', open: false,
    layers: [
      { key: 'stars', name: '星空银河', desc: '太空星点背景', icon: '⭐' },
      { key: 'sunmoon', name: '太阳/月球', desc: '天体位置示意', icon: '🌞' },
      { key: 'bloom', name: '泛光增亮', desc: '亮度/饱和度增强', icon: '🔆' },
      { key: 'fog', name: '景深雾', desc: '边缘暗角纵深', icon: '🌫️' },
      { key: 'graticule', name: '经纬网格', desc: '15° 参考网格', icon: '🕸️' },
      { key: 'compass', name: '指北针/比例尺', desc: 'HUD 导航控件', icon: '🧭' },
      { key: 'hillshade', name: '地形阴影', desc: '山体立体强化', icon: '⛰' }
    ]
  }
]

/**
 * 场景预设：一键启用推荐图层组合
 * 应用时会将其它图层关闭（整体替换开关状态）
 */
export const SCENE_PRESETS = [
  {
    id: 'edu', name: '🌍 科普教育',
    layers: { terrain: true, bathymetry: true, contour: true, labels: true, graticule: true, daynight: true, water: true }
  },
  {
    id: 'weather', name: '🌦️ 气象监测',
    layers: { clouds: true, wind: true, precipitation: true, airquality: true, typhoons: true, currents: true }
  },
  {
    id: 'tech', name: '🌃 夜间科技',
    layers: { nightlights: true, flights: true, cables: true, cities: true, atmosphere: true, stars: true, timezones: true }
  },
  {
    id: 'econ', name: '📊 人口经济',
    layers: { heatmap: true, nightlights: true, gdp: true, cities: true, borders: true }
  },
  {
    id: 'alive', name: '✨ 活地球',
    layers: { currents: true, wind: true, clouds: true, aurora: true, daynight: true, ships: true, flights: true, sealevel: true }
  }
]
