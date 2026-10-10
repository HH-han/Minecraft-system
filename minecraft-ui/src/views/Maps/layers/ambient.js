/**
 * 渲染氛围图层（7 层）
 * 星空银河 / 太阳月球 / 泛光增亮 / 景深雾 / 经纬网格 / 指北针比例尺 / 地形阴影
 */
import * as THREE from 'three'
import topoImg from '../assets/earth-topology.png'
import { SEA_LEVEL, makeGlowTexture, buildGraticuleGeometry } from './util.js'
import { sunDirection, moonDirection, subsolarPoint } from './sunCalc.js'

/* ---------- 星空银河（程序化星点 + 银河带） ---------- */
function buildStars(ctx) {
  const { radius } = ctx
  const R = radius * 32
  const N = 4200
  const pos = new Float32Array(N * 3)
  const col = new Float32Array(N * 3)
  const size = new Float32Array(N)
  const phase = new Float32Array(N)
  // 银河带基向量（倾斜的大圆平面）
  const a = new THREE.Vector3(1, 0.25, 0.1).normalize()
  const b = new THREE.Vector3(-0.2, 0.1, 1).normalize().cross(a).normalize()
  const n = new THREE.Vector3().crossVectors(a, b)
  const v = new THREE.Vector3()
  for (let i = 0; i < N; i++) {
    if (i < N * 0.55) {
      // 均匀星空
      v.set(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1)
      if (v.lengthSq() < 0.001 || v.lengthSq() > 1) { i--; continue }
      v.normalize()
    } else {
      // 银河带：沿大圆高斯分布
      const th = Math.random() * Math.PI * 2
      const spread = (Math.random() + Math.random() + Math.random() - 1.5) * 0.35
      v.copy(a).multiplyScalar(Math.cos(th)).addScaledVector(b, Math.sin(th)).addScaledVector(n, spread).normalize()
    }
    pos.set([v.x * R, v.y * R, v.z * R], i * 3)
    const warm = Math.random()
    const bright = 0.45 + Math.random() * 0.55
    col.set(
      warm > 0.85 ? [bright, bright * 0.85, bright * 0.7] :
      warm < 0.15 ? [bright * 0.75, bright * 0.85, bright] :
      [bright, bright, bright], i * 3
    )
    size[i] = 0.8 + Math.pow(Math.random(), 3) * 2.6
    phase[i] = Math.random() * Math.PI * 2
  }
  const geo = new THREE.BufferGeometry()
  geo.setAttribute('position', new THREE.BufferAttribute(pos, 3))
  geo.setAttribute('color', new THREE.BufferAttribute(col, 3))
  geo.setAttribute('aSize', new THREE.BufferAttribute(size, 1))
  geo.setAttribute('aPhase', new THREE.BufferAttribute(phase, 1))
  const mat = new THREE.ShaderMaterial({
    uniforms: { uTime: { value: 0 }, uOpacity: { value: 0.95 } },
    vertexShader: `
      attribute float aSize; attribute float aPhase;
      uniform float uTime;
      varying vec3 vColor; varying float vT;
      void main(){
        vColor = color;
        vT = 0.8 + 0.2 * sin(uTime * 0.8 + aPhase);
        vec4 mv = modelViewMatrix * vec4(position, 1.0);
        gl_PointSize = aSize;
        gl_Position = projectionMatrix * mv;
      }
    `,
    fragmentShader: `
      uniform float uOpacity;
      varying vec3 vColor; varying float vT;
      void main(){
        float a = smoothstep(0.5, 0.05, length(gl_PointCoord - 0.5)) * uOpacity * vT;
        gl_FragColor = vec4(vColor, a);
      }
    `,
    transparent: true, depthWrite: false, vertexColors: true, blending: THREE.AdditiveBlending
  })
  const pointsObj = new THREE.Points(geo, mat)
  pointsObj.frustumCulled = false
  pointsObj.renderOrder = -10
  const group = new THREE.Group().add(pointsObj)
  return { group, update: t => { mat.uniforms.uTime.value = t }, dispose() { geo.dispose(); mat.dispose() } }
}

/* ---------- 太阳/月球天体 ---------- */
function buildSunmoon(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  const sunTex = makeGlowTexture('rgba(255,244,214,1)', 'rgba(255,196,110,0)')
  const sunMat = new THREE.SpriteMaterial({ map: sunTex, transparent: true, depthWrite: false, blending: THREE.AdditiveBlending, opacity: 0.95 })
  const sun = new THREE.Sprite(sunMat)
  sun.scale.setScalar(radius * 1.1)
  group.add(sun)

  const moonGeo = new THREE.SphereGeometry(radius * 0.14, 24, 24)
  const moonMat = new THREE.MeshPhongMaterial({ color: 0xd8d8e0, emissive: 0x222228, shininess: 4 })
  const moon = new THREE.Mesh(moonGeo, moonMat)
  group.add(moon)

  // 直射点小标记
  const markTex = makeGlowTexture('rgba(255,255,255,0.9)', 'rgba(255,220,150,0)')
  const markMat = new THREE.SpriteMaterial({ map: markTex, transparent: true, depthWrite: false, blending: THREE.AdditiveBlending, opacity: 0.8 })
  const mark = new THREE.Sprite(markMat)
  mark.scale.setScalar(radius * 0.09)
  group.add(mark)

  const sv = new THREE.Vector3()
  const mv = new THREE.Vector3()
  return {
    group,
    update() {
      const now = new Date()
      sunDirection(now, sv)
      sun.position.copy(sv).multiplyScalar(radius * 6)
      moonDirection(now, mv)
      moon.position.copy(mv).multiplyScalar(radius * 3.2)
      const sp = subsolarPoint(now)
      const p = sunDirection(now)
      mark.position.copy(p).multiplyScalar(radius * 1.02)
      mark.material.opacity = 0.5 + 0.3 * Math.sin(now.getTime() / 800)
    },
    dispose() {
      sunTex.dispose(); markTex.dispose(); sunMat.dispose(); markMat.dispose()
      moonGeo.dispose(); moonMat.dispose()
    }
  }
}

/* ---------- 泛光增亮（CSS filter，作用于 WebGL canvas） ---------- */
function buildBloom(ctx) {
  const { container } = ctx
  const apply = on => {
    const canvas = container.querySelector('canvas')
    if (canvas) canvas.style.filter = on ? 'brightness(1.14) saturate(1.2) contrast(1.04)' : ''
  }
  return {
    el: null,
    onShow: () => apply(true),
    onHide: () => apply(false),
    group: new THREE.Group(),
    dispose: () => apply(false)
  }
}

/* ---------- 景深雾（边缘暗角 overlay） ---------- */
function buildFog(ctx) {
  const { container } = ctx
  const el = document.createElement('div')
  el.style.cssText = `
    position: absolute; inset: 0; pointer-events: none; z-index: 3;
    background: radial-gradient(ellipse at center, transparent 52%, rgba(2,4,12,0.42) 100%);
  `
  const group = new THREE.Group()
  return {
    el, group,
    dispose: () => el.remove()
  }
}

/* ---------- 经纬网格 ---------- */
function buildGraticule(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  const mk = (opts, color, opacity) => {
    const geo = buildGraticuleGeometry(radius, opts)
    const mat = new THREE.LineBasicMaterial({ color, transparent: true, opacity })
    const line = new THREE.LineSegments(geo, mat)
    group.add(line)
    return [geo, mat]
  }
  const dim = mk({ latStep: 15, lngStep: 15, alt: 0.002, latRange: [-75, 75] }, 0x88aacc, 0.14)
  const strong = mk({ latStep: 90, lngStep: 30, alt: 0.0022, latRange: [0, 0] }, 0x88aacc, 0.3)
  return {
    group,
    dispose() { dim.forEach(x => x.dispose()); strong.forEach(x => x.dispose()) }
  }
}

/* ---------- 指北针 / 比例尺 HUD ---------- */
function buildCompass(ctx) {
  const { container, globe, radius } = ctx
  const el = document.createElement('div')
  el.style.cssText = `
    position: absolute; right: 76px; bottom: 24px; z-index: 5; pointer-events: none;
    display: flex; flex-direction: column; align-items: center; gap: 6px;
    font-family: -apple-system, 'PingFang SC', sans-serif;
  `
  el.innerHTML = `
    <svg id="globe-compass-n" width="34" height="34" viewBox="0 0 34 34" style="filter: drop-shadow(0 1px 3px rgba(0,0,0,0.4));">
      <polygon points="17,3 21,19 17,16 13,19" fill="#ff5e5e"/>
      <polygon points="17,31 21,15 17,18 13,15" fill="#ffffff"/>
      <circle cx="17" cy="17" r="2" fill="#ffffff"/>
      <text x="17" y="11" font-size="7" fill="#fff" text-anchor="middle" style="paint-order:stroke;stroke:rgba(0,0,0,0.5);stroke-width:2px;">N</text>
    </svg>
    <div style="display:flex; align-items:center; gap:6px;">
      <div id="globe-scale-bar" style="height:4px; border:1px solid rgba(255,255,255,0.8); border-top:none; width:80px; box-shadow:0 1px 3px rgba(0,0,0,0.4);"></div>
      <span id="globe-scale-text" style="font-size:10px; color:#fff; text-shadow:0 1px 3px rgba(0,0,0,0.6);">500 km</span>
    </div>
  `
  container.appendChild(el)
  const arrow = el.querySelector('#globe-compass-n')
  const bar = el.querySelector('#globe-scale-bar')
  const text = el.querySelector('#globe-scale-text')

  const NICE_KM = [50, 100, 200, 500, 1000, 2000, 5000, 10000]
  let acc = 0
  return {
    el, group: new THREE.Group(),
    update(dt) {
      if (!globe) return
      acc += dt
      if (acc < 0.25) return
      acc = 0
      try {
        const controls = globe.controls()
        if (controls?.getAzimuthalAngle != null) {
          const az = controls.getAzimuthalAngle()
          arrow.style.transform = `rotate(${-az}rad)`
        }
        const cam = globe.camera()
        const dist = cam.position.length()
        const heightPx = container.clientHeight || 600
        const kmPerUnit = 6371 / radius
        const kmPerPx = (2 * dist * Math.tan(cam.fov * Math.PI / 360) / heightPx) * kmPerUnit
        let best = NICE_KM[0]
        for (const k of NICE_KM) { if (k / kmPerPx <= 110) best = k }
        bar.style.width = `${Math.max(24, Math.min(110, best / kmPerPx))}px`
        text.textContent = best >= 1000 ? `${best / 1000} 万米` : `${best} km`
      } catch (e) { /* 忽略 HUD 更新异常 */ }
    },
    dispose: () => el.remove()
  }
}

/* ---------- 地形阴影（topo 梯度 lambert 着色） ---------- */
function buildHillshade(ctx) {
  const { radius } = ctx
  const tex = new THREE.TextureLoader().load(topoImg)
  tex.wrapS = THREE.RepeatWrapping
  const material = new THREE.ShaderMaterial({
    uniforms: {
      uMap: { value: tex },
      uTexel: { value: new THREE.Vector2(1 / 1024, 1 / 512) },
      uOpacity: { value: 0.55 }
    },
    vertexShader: `
      varying vec2 vUv;
      void main(){ vUv = uv; gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0); }
    `,
    fragmentShader: `
      uniform sampler2D uMap; uniform vec2 uTexel; uniform float uOpacity;
      varying vec2 vUv;
      void main(){
        float e = texture2D(uMap, vUv).r;
        if (e < ${SEA_LEVEL}) discard;
        float dx = texture2D(uMap, vUv + vec2(uTexel.x, 0.0)).r - texture2D(uMap, vUv - vec2(uTexel.x, 0.0)).r;
        float dy = texture2D(uMap, vUv + vec2(0.0, uTexel.y)).r - texture2D(uMap, vUv - vec2(0.0, uTexel.y)).r;
        vec3 n = normalize(vec3(-dx * 60.0, -dy * 60.0, 1.0));
        vec3 light = normalize(vec3(-0.55, 0.5, 0.85));
        float sh = clamp(dot(n, light), 0.0, 1.0);
        float shade = 0.5 + 0.5 * sh;
        gl_FragColor = vec4(vec3(shade), uOpacity);
      }
    `,
    transparent: true, depthWrite: false
  })
  const mesh = new THREE.Mesh(new THREE.SphereGeometry(radius * 1.0035, 96, 96), material)
  const group = new THREE.Group().add(mesh)
  return { group, dispose() { mesh.geometry.dispose(); material.dispose() } }
}

export const defs = {
  stars: { build: buildStars },
  sunmoon: { build: buildSunmoon },
  bloom: { build: buildBloom },
  fog: { build: buildFog },
  graticule: { build: buildGraticule },
  compass: { build: buildCompass },
  hillshade: { build: buildHillshade }
}
