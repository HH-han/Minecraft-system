/**
 * 环境动态图层（8 层）
 * 空气质量 / 碳排放 / 海平面上升 / 台风路径 / 野火热点 / 船舶航迹 / 卫星轨道 / 昼夜分界
 */
import * as THREE from 'three'
import { toVec3, pathsToLineSegments, makeSoftPoints, makeFlowParticles, makeGlowTexture, GLSL_NOISE } from './util.js'
import { sunDirection } from './sunCalc.js'
import { AIR_QUALITY, CO2_SOURCES, TYPHOON_TRACKS, WILDFIRES, SHIPPING_LANES, SATELLITE_ORBITS } from './data.js'

/* ---------- 空气质量（PM2.5 热点） ---------- */
function aqiColor(aqi) {
  if (aqi < 50) return '#4caf50'
  if (aqi < 100) return '#ffeb3b'
  if (aqi < 150) return '#ff9800'
  if (aqi < 200) return '#f44336'
  return '#9c27b0'
}

function buildAirquality(ctx) {
  const { radius } = ctx
  const pts = AIR_QUALITY.map(([name, lat, lng, aqi]) => ({
    lat, lng, alt: 0.005,
    color: aqiColor(aqi),
    size: 3 + aqi * 0.028,
    phase: Math.random() * Math.PI * 2
  }))
  const soft = makeSoftPoints(radius, pts, { opacity: 0.6, pulse: 0.2, pulseFreq: 0.8 })
  const group = new THREE.Group().add(soft.object)
  return { group, update: soft.update, dispose: soft.dispose }
}

/* ---------- 碳排放：热点上升粒子 ---------- */
function buildCo2(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  const PER = 16
  const particles = [] // {srcIdx, p, speed, swirl}
  CO2_SOURCES.forEach(([lat, lng, w]) => {
    for (let i = 0; i < PER; i++) {
      particles.push({
        lat, lng, w,
        p: Math.random(),
        speed: 0.06 + Math.random() * 0.08,
        swirl: Math.random() * Math.PI * 2
      })
    }
  })
  const n = particles.length
  const pos = new Float32Array(n * 3)
  const col = new Float32Array(n * 3)
  const size = new Float32Array(n)
  const phase = new Float32Array(n)
  const geo = new THREE.BufferGeometry()
  geo.setAttribute('position', new THREE.BufferAttribute(pos, 3))
  geo.setAttribute('aColor', new THREE.BufferAttribute(col, 3))
  geo.setAttribute('aSize', new THREE.BufferAttribute(size, 1))
  geo.setAttribute('aPhase', new THREE.BufferAttribute(phase, 1))
  const mat = new THREE.ShaderMaterial({
    uniforms: { uOpacity: { value: 0.85 } },
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
        float a = smoothstep(0.5, 0.12, length(gl_PointCoord - 0.5)) * uOpacity;
        if (a < 0.01) discard;
        gl_FragColor = vec4(vColor, a);
      }
    `,
    transparent: true, depthWrite: false, blending: THREE.AdditiveBlending
  })
  const pointsObj = new THREE.Points(geo, mat)
  pointsObj.frustumCulled = false
  group.add(pointsObj)

  const base = new THREE.Color('#9aa7b5')
  const v = new THREE.Vector3()
  const up = new THREE.Vector3()
  const tang = new THREE.Vector3()
  const origin = new THREE.Vector3(0, 0, 0)
  return {
    group,
    update(dt) {
      for (let k = 0; k < n; k++) {
        const P = particles[k]
        P.p += P.speed * dt
        if (P.p > 1) P.p -= 1
        toVec3(radius, P.lat, P.lng, 0.002, v)
        up.copy(v).normalize()
        // 切向摆动模拟扩散
        tang.set(0, 1, 0).cross(up).normalize().multiplyScalar(Math.sin(P.p * 9 + P.swirl) * radius * 0.012 * P.p)
        v.addScaledVector(up, radius * (0.004 + P.p * 0.05)).add(tang)
        pos.set([v.x, v.y, v.z], k * 3)
        const fade = (1 - P.p) * P.w
        col.set([base.r * fade, base.g * fade * 0.95, base.b * fade * 0.9], k * 3)
        size[k] = 1.5 + P.p * 2.6
      }
      geo.attributes.position.needsUpdate = true
      geo.attributes.aColor.needsUpdate = true
      geo.attributes.aSize.needsUpdate = true
    },
    dispose() { geo.dispose(); mat.dispose() }
  }
}

/* ---------- 海平面上升模拟（半透明水壳缓慢涨落） ---------- */
function buildSealevel(ctx) {
  const { radius } = ctx
  const mat = new THREE.MeshBasicMaterial({
    color: 0x2f8fd8, transparent: true, opacity: 0.22,
    depthWrite: false, side: THREE.FrontSide
  })
  const mesh = new THREE.Mesh(new THREE.SphereGeometry(radius * 1.004, 96, 96), mat)
  const group = new THREE.Group().add(mesh)
  return {
    group,
    update(t) {
      // 缓慢的上升-回退周期，模拟不同淹没情景
      const k = 0.5 + 0.5 * Math.sin(t * 0.12)
      mesh.scale.setScalar(1 + k * 0.008)
      mat.opacity = 0.14 + 0.14 * k
    },
    dispose() { mesh.geometry.dispose(); mat.dispose() }
  }
}

/* ---------- 台风/气旋路径：轨迹线 + 移动螺旋符号 ---------- */
function buildTyphoons(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  const lineGeo = pathsToLineSegments(TYPHOON_TRACKS.map(t => t.path), radius, 0.006)
  const lineMat = new THREE.LineBasicMaterial({ color: 0xffb347, transparent: true, opacity: 0.55 })
  group.add(new THREE.LineSegments(lineGeo, lineMat))

  // 螺旋符号纹理
  const canvas = document.createElement('canvas')
  canvas.width = canvas.height = 96
  const c2d = canvas.getContext('2d')
  c2d.strokeStyle = 'rgba(255,255,255,0.95)'
  c2d.lineWidth = 4
  c2d.lineCap = 'round'
  for (let arm = 0; arm < 2; arm++) {
    c2d.beginPath()
    for (let i = 0; i <= 40; i++) {
      const t = i / 40
      const a = arm * Math.PI + t * Math.PI * 1.6
      const r = 4 + t * 34
      const x = 48 + Math.cos(a) * r
      const y = 48 + Math.sin(a) * r
      i === 0 ? c2d.moveTo(x, y) : c2d.lineTo(x, y)
    }
    c2d.stroke()
  }
  const tex = new THREE.CanvasTexture(canvas)
  tex.colorSpace = THREE.SRGBColorSpace
  const spriteMat = new THREE.SpriteMaterial({ map: tex, transparent: true, depthWrite: false, color: 0x9fd8ff, opacity: 0.95 })

  const movers = TYPHOON_TRACKS.map((tr, i) => {
    const sp = new THREE.Sprite(spriteMat.clone())
    sp.scale.setScalar(radius * 0.045)
    sp.position.set(0, 0, 0)
    group.add(sp)
    return { tr, sp, t: i / TYPHOON_TRACKS.length, speed: 0.014 + i * 0.004 }
  })

  const v = new THREE.Vector3()
  function sampleAlong(path, t) {
    const f = t * (path.length - 1)
    const i = Math.min(path.length - 2, Math.floor(f))
    const k = f - i
    return [path[i][0] + (path[i + 1][0] - path[i][0]) * k, path[i][1] + (path[i + 1][1] - path[i][1]) * k]
  }
  return {
    group,
    update(dt) {
      for (const m of movers) {
        m.t = (m.t + m.speed * dt) % 1
        const [la, ln] = sampleAlong(m.tr.path, m.t)
        toVec3(radius, la, ln, 0.012, v)
        m.sp.position.copy(v)
        m.sp.material.rotation += dt * 5
      }
    },
    dispose() {
      lineGeo.dispose(); lineMat.dispose(); tex.dispose()
      movers.forEach(m => m.sp.material.dispose())
    }
  }
}

/* ---------- 野火热点（闪烁） ---------- */
function buildWildfires(ctx) {
  const { radius } = ctx
  const pts = WILDFIRES.map(([lat, lng]) => ({
    lat, lng, alt: 0.005,
    color: Math.random() > 0.6 ? '#ffd166' : '#ff5e3a',
    size: 2.4 + Math.random() * 2.4,
    phase: Math.random() * Math.PI * 2
  }))
  const soft = makeSoftPoints(radius, pts, { opacity: 0.95, pulse: 0.7, pulseFreq: 6 })
  const group = new THREE.Group().add(soft.object)
  return { group, update: soft.update, dispose: soft.dispose }
}

/* ---------- 船舶航迹（海运流线） ---------- */
function buildShips(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  const laneGeo = pathsToLineSegments(SHIPPING_LANES.map(l => l.path), radius, 0.004)
  const laneMat = new THREE.LineBasicMaterial({ color: 0x8ab4c8, transparent: true, opacity: 0.22 })
  group.add(new THREE.LineSegments(laneGeo, laneMat))
  const flow = makeFlowParticles(radius, SHIPPING_LANES.map(l => l.path), {
    count: 200, size: 3, alt: 0.006, speed: 0.05, color: '#cfe8ff', opacity: 0.9
  })
  group.add(flow.object)
  return { group, update: flow.update, dispose() { flow.dispose(); laneGeo.dispose(); laneMat.dispose() } }
}

/* ---------- 卫星轨道/星座 ---------- */
function buildSatellites(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  const movers = []
  const orbitMatCache = new Map()
  for (const orb of SATELLITE_ORBITS) {
    const r = radius * (1 + orb.alt)
    const rot = new THREE.Matrix4()
      .makeRotationZ(orb.inc * Math.PI / 180)
      .multiply(new THREE.Matrix4().makeRotationY(Math.random() * Math.PI * 2))
    // 轨道线圈
    const segs = 128
    const pos = []
    for (let i = 0; i <= segs; i++) {
      const a = i / segs * Math.PI * 2
      const p = new THREE.Vector3(Math.cos(a) * r, 0, Math.sin(a) * r).applyMatrix4(rot)
      pos.push(p.x, p.y, p.z)
    }
    const geo = new THREE.BufferGeometry()
    geo.setAttribute('position', new THREE.Float32BufferAttribute(pos, 3))
    let mat = orbitMatCache.get(orb.color)
    if (!mat) {
      mat = new THREE.LineBasicMaterial({ color: orb.color, transparent: true, opacity: 0.22 })
      orbitMatCache.set(orb.color, mat)
    }
    group.add(new THREE.Line(geo, mat))

    // 卫星小球
    const satGeo = new THREE.SphereGeometry(radius * 0.008, 8, 8)
    const satMat = new THREE.MeshBasicMaterial({ color: orb.color })
    for (let i = 0; i < orb.count; i++) {
      const mesh = new THREE.Mesh(satGeo, satMat)
      group.add(mesh)
      movers.push({ mesh, rot, r, phase: i / orb.count * Math.PI * 2, speed: orb.speed })
    }
  }
  return {
    group,
    update(t) {
      for (const m of movers) {
        const a = m.phase + t * m.speed
        m.mesh.position.set(Math.cos(a) * m.r, 0, Math.sin(a) * m.r).applyMatrix4(m.rot)
      }
    },
    dispose() {
      group.traverse(o => {
        o.geometry?.dispose?.()
        o.material?.dispose?.()
      })
    }
  }
}

/* ---------- 昼夜分界（实时晨昏线夜半球遮罩） ---------- */
function buildDaynight(ctx) {
  const { radius } = ctx
  const sunDir = new THREE.Vector3(1, 0, 0)
  const material = new THREE.ShaderMaterial({
    uniforms: {
      uSunDir: { value: sunDir },
      uOpacity: { value: 0.5 }
    },
    vertexShader: `
      varying vec3 vWorldN;
      void main(){
        vWorldN = normalize(mat3(modelMatrix) * normal);
        gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
      }
    `,
    fragmentShader: `
      uniform vec3 uSunDir; uniform float uOpacity;
      varying vec3 vWorldN;
      void main(){
        float nd = dot(vWorldN, uSunDir);
        // 晨昏线两侧平滑过渡（约 ±5°）
        float night = smoothstep(0.09, -0.09, nd);
        float a = night * uOpacity;
        if (a < 0.01) discard;
        gl_FragColor = vec4(vec3(0.01, 0.02, 0.08), a);
      }
    `,
    transparent: true, depthWrite: false
  })
  const mesh = new THREE.Mesh(new THREE.SphereGeometry(radius * 1.003, 96, 96), material)
  const group = new THREE.Group().add(mesh)
  return {
    group,
    update() {
      sunDirection(new Date(), sunDir)
    },
    dispose() { mesh.geometry.dispose(); material.dispose() }
  }
}

export const defs = {
  airquality: { build: buildAirquality },
  co2: { build: buildCo2 },
  sealevel: { build: buildSealevel },
  typhoons: { build: buildTyphoons },
  wildfires: { build: buildWildfires },
  ships: { build: buildShips },
  satellites: { build: buildSatellites },
  daynight: { build: buildDaynight }
}
