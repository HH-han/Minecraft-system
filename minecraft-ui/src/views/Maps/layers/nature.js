/**
 * 自然地理图层（10 层）
 * 海底地形 / 河流湖泊 / 冰川积雪 / 植被指数 / 土地覆盖 / 洋流 / 风场 / 降水带 / 地震火山 / 极光带
 */
import * as THREE from 'three'
import topoImg from '../assets/earth-topology.png'
import { SEA_LEVEL, toVec3, pathsToLineSegments, makeSoftPoints, makeFlowParticles, GLSL_NOISE, loadImage, fbm2 } from './util.js'
import { RIVERS, LAKES, CURRENTS, RING_OF_FIRE, VOLCANOES } from './data.js'

/* ---------- 海底地形（bathymetry）：基于 topo 贴图的海面以下深度渲染 ---------- */
function buildBathymetry(ctx) {
  const { radius } = ctx
  const tex = new THREE.TextureLoader().load(topoImg)
  const material = new THREE.ShaderMaterial({
    uniforms: {
      uMap: { value: tex },
      uSea: { value: SEA_LEVEL },
      uOpacity: { value: 0.85 }
    },
    vertexShader: `
      varying vec2 vUv;
      void main(){ vUv = uv; gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0); }
    `,
    fragmentShader: `
      uniform sampler2D uMap;
      uniform float uSea;
      uniform float uOpacity;
      varying vec2 vUv;
      void main(){
        float elev = texture2D(uMap, vUv).r;
        if (elev >= uSea) discard;
        float depth = clamp(1.0 - elev / uSea, 0.0, 1.0);
        vec3 shallow = vec3(0.20, 0.55, 0.75);
        vec3 mid     = vec3(0.06, 0.24, 0.48);
        vec3 deep    = vec3(0.01, 0.05, 0.15);
        vec3 col = depth < 0.5 ? mix(shallow, mid, depth * 2.0) : mix(mid, deep, depth * 2.0 - 1.0);
        // 深度等值线
        float f = fract(depth * 22.0);
        float d = min(f, 1.0 - f);
        float line = 1.0 - smoothstep(0.0, 0.045, d);
        col += line * 0.10;
        gl_FragColor = vec4(col, uOpacity);
      }
    `,
    transparent: true,
    depthWrite: false
  })
  const mesh = new THREE.Mesh(new THREE.SphereGeometry(radius * 1.0012, 96, 96), material)
  const group = new THREE.Group().add(mesh)
  return { group, dispose() { mesh.geometry.dispose(); material.dispose() } }
}

/* ---------- 河流/湖泊 ---------- */
function buildWater(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  // 河流
  const riverGeo = pathsToLineSegments(RIVERS.map(r => r.path), radius, 0.004)
  const riverMat = new THREE.LineBasicMaterial({ color: 0x3fa7ff, transparent: true, opacity: 0.85 })
  group.add(new THREE.LineSegments(riverGeo, riverMat))
  // 湖泊（切向圆面）
  const lakeMat = new THREE.MeshBasicMaterial({ color: 0x57c7ff, transparent: true, opacity: 0.55, side: THREE.DoubleSide, depthWrite: false })
  for (const [name, lat, lng, rDeg] of LAKES) {
    const geo = new THREE.CircleGeometry(radius * rDeg * Math.PI / 180 * 2.2, 24)
    const mesh = new THREE.Mesh(geo, lakeMat)
    toVec3(radius, lat, lng, 0.003, mesh.position)
    mesh.lookAt(mesh.position.clone().multiplyScalar(2))
    group.add(mesh)
  }
  return {
    group,
    dispose() { riverGeo.dispose(); riverMat.dispose(); lakeMat.dispose(); group.children.forEach(m => m.geometry?.dispose()) }
  }
}

/* ---------- 冰川/积雪：两极冰盖 + 季节脉动 ---------- */
function buildIce(ctx) {
  const { radius } = ctx
  const uniforms = { uTime: { value: 0 }, uBase: { value: 0.88 } }
  const makeCap = (thetaStart, thetaLength) => {
    const mat = new THREE.ShaderMaterial({
      uniforms,
      vertexShader: `
        varying vec2 vUv;
        void main(){ vUv = uv; gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0); }
      `,
      fragmentShader: `
        uniform float uTime; uniform float uBase;
        varying vec2 vUv;
        ${GLSL_NOISE}
        void main(){
          // 边缘不规则 + 季节消长
          float season = 0.06 * sin(uTime * 0.05);
          float edge = vUv.y + (gfbm(vec2(vUv.x * 22.0, uTime * 0.02)) - 0.5) * 0.28 - season;
          float a = uBase * smoothstep(1.0, 0.62, edge);
          if (a < 0.02) discard;
          vec3 col = mix(vec3(0.92, 0.96, 1.0), vec3(0.72, 0.84, 0.95), gfbm(vUv * vec2(30.0, 6.0)));
          gl_FragColor = vec4(col, a);
        }
      `,
      transparent: true, depthWrite: false
    })
    const mesh = new THREE.Mesh(new THREE.SphereGeometry(radius * 1.0025, 96, 24, 0, Math.PI * 2, thetaStart, thetaLength), mat)
    mesh.userData.mat = mat
    return mesh
  }
  const north = makeCap(0, 25 * Math.PI / 180)          // 北极：lat 65~90
  const south = makeCap(152 * Math.PI / 180, 28 * Math.PI / 180) // 南极：lat -63~-90
  const group = new THREE.Group().add(north, south)
  return {
    group,
    update(t) { uniforms.uTime.value = t; uniforms.uBase.value = 0.88 + 0.07 * Math.sin(t * 0.05) },
    dispose() { [north, south].forEach(m => { m.geometry.dispose(); m.userData.mat.dispose() }) }
  }
}

/* ---------- 植被指数 / 土地覆盖（由 topo 贴图 + 噪声程序化生成 Canvas 纹理） ---------- */
function inDesertBox(lat, lng) {
  return (lat > 15 && lat < 31 && lng > -16 && lng < 58) ||   // 撒哈拉
    (lat > 12 && lat < 31 && lng > 34 && lng < 62) ||        // 阿拉伯
    (lat > 37 && lat < 48 && lng > 74 && lng < 112) ||       // 戈壁
    (lat > -31 && lat < -19 && lng > 118 && lng < 142) ||    // 澳洲内陆
    (lat > -30 && lat < -20 && lng > 14 && lng < 22)         // 纳米布
}

function biomeGreenness(lat, lng, elev, nz) {
  const alat = Math.abs(lat)
  let g
  if (alat < 12) g = 0.85                                   // 热带雨林
  else if (alat < 35) g = 0.55                              // 亚热带
  else if (alat < 58) g = 0.45                              // 温带
  else g = 0.16                                             // 苔原
  if (inDesertBox(lat, lng)) g = 0.06
  if (elev > 0.8) g *= 0.35                                 // 高山
  g *= 0.6 + 0.4 * nz
  return Math.min(1, g)
}

async function buildBiomeOverlay(ctx, mode) {
  const { radius } = ctx
  const group = new THREE.Group()
  let mesh = null
  try {
    const img = await loadImage(topoImg)
    const w = 1024, h = 512
    const canvas = document.createElement('canvas')
    canvas.width = w; canvas.height = h
    const c2d = canvas.getContext('2d')
    c2d.drawImage(img, 0, 0, w, h)
    const src = c2d.getImageData(0, 0, w, h)
    const out = c2d.createImageData(w, h)
    for (let y = 0; y < h; y++) {
      const lat = 90 - (y / h) * 180
      for (let x = 0; x < w; x++) {
        const i = (y * w + x) * 4
        const elev = src.data[i] / 255
        const o = i
        if (elev < SEA_LEVEL) { out.data[o + 3] = 0; continue } // 海洋透明
        const lng = (x / w) * 360 - 180
        const nz = fbm2(x * 0.02, y * 0.02, 4, 11)
        const g = biomeGreenness(lat, lng, elev, nz)
        let r, gg, b, a
        if (mode === 'ndvi') {
          // 棕(裸地) → 深绿(茂密植被)
          r = 0.55 - 0.5 * g; gg = 0.45 + 0.15 * g; b = 0.28 - 0.18 * g
          a = 0.72
        } else {
          // 土地覆盖分类
          if (Math.abs(lat) > 66 || elev > 0.82) { r = 0.93; gg = 0.95; b = 0.97 }      // 冰雪
          else if (g < 0.14) { r = 0.85; gg = 0.72; b = 0.5 }                           // 荒漠
          else if (g < 0.35) { r = 0.62; gg = 0.66; b = 0.42 }                          // 草原
          else if (g < 0.62) { r = 0.3; gg = 0.55; b = 0.28 }                           // 疏林
          else { r = 0.14; gg = 0.42; b = 0.16 }                                        // 密林
          if (Math.abs(lat) > 58) { r = 0.66; gg = 0.69; b = 0.62 }                     // 苔原
          a = 0.85
        }
        out.data[o] = r * 255; out.data[o + 1] = gg * 255; out.data[o + 2] = b * 255
        out.data[o + 3] = a * 255
      }
    }
    c2d.putImageData(out, 0, 0)
    const tex = new THREE.CanvasTexture(canvas)
    tex.colorSpace = THREE.SRGBColorSpace
    const mat = new THREE.MeshBasicMaterial({ map: tex, transparent: true, depthWrite: false })
    mesh = new THREE.Mesh(new THREE.SphereGeometry(radius * 1.0018, 96, 96), mat)
    group.add(mesh)
  } catch (e) { console.warn('生物群系纹理生成失败:', e) }
  return {
    group,
    dispose() { if (mesh) { mesh.geometry.dispose(); mesh.material.map?.dispose(); mesh.material.dispose() } }
  }
}

function buildNDVI(ctx) { return buildBiomeOverlay(ctx, 'ndvi') }
function buildLandcover(ctx) { return buildBiomeOverlay(ctx, 'landcover') }

/* ---------- 洋流：动态流线粒子 ---------- */
function buildCurrents(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  // 洋流走向线（暖流橙/寒流蓝，弱透明）
  const warm = new THREE.LineBasicMaterial({ color: 0xff9a5a, transparent: true, opacity: 0.3 })
  const cold = new THREE.LineBasicMaterial({ color: 0x54b9ff, transparent: true, opacity: 0.3 })
  CURRENTS.forEach(c => {
    const geo = pathsToLineSegments([c.path], radius, 0.005)
    group.add(new THREE.LineSegments(geo, c.warm ? warm : cold))
  })
  const flow = makeFlowParticles(radius, CURRENTS.map(c => c.path), {
    count: 320, size: 3.4, alt: 0.006, speed: 0.045, color: '#7fd4ff', opacity: 0.85
  })
  group.add(flow.object)
  return { group, update: flow.update, dispose() { flow.dispose(); group.children.forEach(m => m.geometry?.dispose?.()) } }
}

/* ---------- 风场：纬向环流粒子 ---------- */
function buildWind(ctx) {
  const { radius } = ctx
  const COUNT = 900
  const state = []
  for (let i = 0; i < COUNT; i++) {
    const lat = (Math.random() * 2 - 1) * 78
    const band = Math.abs(lat) < 30 ? -1 : (Math.abs(lat) < 62 ? 1 : -1)
    state.push({
      lat,
      lng: Math.random() * 360 - 180,
      dir: band,
      speed: 5 + Math.random() * 9,
      meanderAmp: 2 + Math.random() * 4,
      phase: Math.random() * Math.PI * 2,
      alt: 0.008 + Math.random() * 0.02
    })
  }
  const pos = new Float32Array(COUNT * 3)
  const col = new Float32Array(COUNT * 3)
  const size = new Float32Array(COUNT)
  const phase = new Float32Array(COUNT)
  for (let i = 0; i < COUNT; i++) {
    col.set([0.72, 0.88, 1.0], i * 3)
    size[i] = 1.6 + Math.random() * 1.8
    phase[i] = state[i].phase
  }
  const geo = new THREE.BufferGeometry()
  geo.setAttribute('position', new THREE.BufferAttribute(pos, 3))
  geo.setAttribute('aColor', new THREE.BufferAttribute(col, 3))
  geo.setAttribute('aSize', new THREE.BufferAttribute(size, 1))
  geo.setAttribute('aPhase', new THREE.BufferAttribute(phase, 1))
  const mat = new THREE.ShaderMaterial({
    uniforms: { uOpacity: { value: 0.55 } },
    vertexShader: `
      attribute vec3 aColor; attribute float aSize; attribute float aPhase;
      varying vec3 vColor;
      void main(){
        vColor = aColor;
        vec4 mv = modelViewMatrix * vec4(position, 1.0);
        gl_PointSize = aSize * (160.0 / -mv.z);
        gl_Position = projectionMatrix * mv;
      }
    `,
    fragmentShader: `
      uniform float uOpacity; varying vec3 vColor;
      void main(){
        float a = smoothstep(0.5, 0.15, length(gl_PointCoord - 0.5)) * uOpacity;
        if (a < 0.01) discard;
        gl_FragColor = vec4(vColor, a);
      }
    `,
    transparent: true, depthWrite: false, blending: THREE.AdditiveBlending
  })
  const pointsObj = new THREE.Points(geo, mat)
  pointsObj.frustumCulled = false
  const group = new THREE.Group().add(pointsObj)
  const v = new THREE.Vector3()
  return {
    group,
    update(dt) {
      for (let i = 0; i < COUNT; i++) {
        const s = state[i]
        s.lng += s.dir * s.speed * dt
        if (s.lng > 180) s.lng -= 360
        if (s.lng < -180) s.lng += 360
        const wob = Math.sin(s.lng * 0.045 + s.phase) * s.meanderAmp
        toVec3(radius, Math.max(-88, Math.min(88, s.lat + wob)), s.lng, s.alt, v)
        pos.set([v.x, v.y, v.z], i * 3)
      }
      geo.attributes.position.needsUpdate = true
    },
    dispose() { geo.dispose(); mat.dispose() }
  }
}

/* ---------- 降水带：ITCZ 与中纬度风暴带粒子 ---------- */
function buildPrecipitation(ctx) {
  const { radius } = ctx
  const points = []
  const belts = [
    { center: 0, spread: 9, n: 260 },    // 赤道辐合带
    { center: 48, spread: 10, n: 130 },  // 北半球西风带
    { center: -48, spread: 10, n: 130 }, // 南半球西风带
    { center: 22, spread: 6, n: 60 },    // 季风区
    { center: 90, spread: 6, n: 50 },    // 北太平洋西侧
    { center: -90, spread: 6, n: 50 }
  ]
  for (const b of belts) {
    for (let i = 0; i < b.n; i++) {
      let lat = b.center + (Math.random() * 2 - 1) * b.spread
      lat = Math.max(-85, Math.min(85, lat))
      points.push({
        lat, lng: Math.random() * 360 - 180,
        color: '#9fd0ff', size: 1.4 + Math.random() * 1.6,
        alt: 0.006 + Math.random() * 0.012,
        phase: Math.random() * Math.PI * 2
      })
    }
  }
  const soft = makeSoftPoints(radius, points, { opacity: 0.6, pulse: 0.5, pulseFreq: 3.5 })
  const group = new THREE.Group().add(soft.object)
  return { group, update: soft.update, dispose: soft.dispose }
}

/* ---------- 地震带/火山分布 ---------- */
function buildEarthquakes(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  // 环太平洋火山带主线
  const ringGeo = pathsToLineSegments(RING_OF_FIRE, radius, 0.004)
  const ringMat = new THREE.LineBasicMaterial({ color: 0xff5e3a, transparent: true, opacity: 0.35 })
  group.add(new THREE.LineSegments(ringGeo, ringMat))
  // 火山热点脉动点
  const pts = VOLCANOES.map(([lat, lng]) => ({
    lat, lng, color: Math.random() > 0.5 ? '#ff6a3d' : '#ffb347',
    size: 2.6 + Math.random() * 2.2, phase: Math.random() * Math.PI * 2
  }))
  const soft = makeSoftPoints(radius, pts, { opacity: 0.95, pulse: 0.55, pulseFreq: 2.4 })
  group.add(soft.object)
  return {
    group,
    update: soft.update,
    dispose() { soft.dispose(); ringGeo.dispose(); ringMat.dispose() }
  }
}

/* ---------- 极光带：南北两极动态光幕 ---------- */
function buildAurora(ctx) {
  const { radius } = ctx
  const uniforms = { uTime: { value: 0 }, uStrength: { value: 1 } }
  const makeBand = (thetaStart, thetaLength) => {
    const mat = new THREE.ShaderMaterial({
      uniforms,
      vertexShader: `
        varying vec2 vUv;
        void main(){ vUv = uv; gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0); }
      `,
      fragmentShader: `
        uniform float uTime; uniform float uStrength;
        varying vec2 vUv;
        ${GLSL_NOISE}
        void main(){
          // 纵向垂帘：纬度方向的光带轮廓
          float profile = sin(vUv.y * 3.14159);
          // 沿经度流动的波动光幕
          float w1 = gfbm(vec2(vUv.x * 14.0, uTime * 0.12 + vUv.y * 1.4));
          float w2 = gfbm(vec2(vUv.x * 30.0 - uTime * 0.2, vUv.y * 2.0));
          float intensity = profile * (0.35 + 0.65 * w1) * (0.6 + 0.4 * w2);
          // 垂帘条纹
          float curtain = 0.55 + 0.45 * sin(vUv.x * 260.0 + w2 * 9.0);
          intensity *= curtain;
          vec3 green = vec3(0.24, 1.0, 0.55);
          vec3 purple = vec3(0.55, 0.3, 0.95);
          vec3 col = mix(green, purple, clamp(vUv.y * 1.4 - 0.2, 0.0, 1.0));
          float a = clamp(intensity * uStrength, 0.0, 1.0) * 0.75;
          if (a < 0.015) discard;
          gl_FragColor = vec4(col, a);
        }
      `,
      transparent: true, depthWrite: false, side: THREE.DoubleSide, blending: THREE.AdditiveBlending
    })
    const mesh = new THREE.Mesh(
      new THREE.SphereGeometry(radius * 1.02, 96, 24, 0, Math.PI * 2, thetaStart, thetaLength),
      mat
    )
    mesh.userData.mat = mat
    return mesh
  }
  // 北极光带 lat 58~76，南极光带 lat -58~-76
  const north = makeBand((90 - 76) * Math.PI / 180, 18 * Math.PI / 180)
  const south = makeBand((90 - 14) * Math.PI / 180, 18 * Math.PI / 180)
  const group = new THREE.Group().add(north, south)
  return {
    group,
    update(t) {
      uniforms.uTime.value = t
      uniforms.uStrength.value = 0.8 + 0.35 * Math.sin(t * 0.23) + 0.15 * Math.sin(t * 0.71)
    },
    dispose() { [north, south].forEach(m => { m.geometry.dispose(); m.userData.mat.dispose() }) }
  }
}

export const defs = {
  bathymetry: { build: buildBathymetry },
  water: { build: buildWater },
  ice: { build: buildIce },
  ndvi: { build: buildNDVI },
  landcover: { build: buildLandcover },
  currents: { build: buildCurrents },
  wind: { build: buildWind },
  precipitation: { build: buildPrecipitation },
  earthquakes: { build: buildEarthquakes },
  aurora: { build: buildAurora }
}
