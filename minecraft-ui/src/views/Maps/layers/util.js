/**
 * 图层工具集：坐标转换 / 软粒子着色器 / 流线粒子 / 文本精灵 / JS 噪声
 * 坐标公式与 three-globe 的 polar2Cartesian 保持一致，确保与球面纹理对齐
 */
import * as THREE from 'three'

export const SEA_LEVEL = 0.045 // topo 贴图中海平面编码阈值

/** lat/lng(+相对高度) → 场景坐标（radius 为球体场景半径） */
export function toVec3(radius, lat, lng, alt = 0, out = new THREE.Vector3()) {
  const r = radius * (1 + alt)
  const phi = (90 - lat) * Math.PI / 180
  const theta = (90 - lng) * Math.PI / 180
  return out.set(
    r * Math.sin(phi) * Math.cos(theta),
    r * Math.cos(phi),
    r * Math.sin(phi) * Math.sin(theta)
  )
}

/** 将 [[lat,lng],...] 路径转为 LineSegments 顶点（相邻点连线，可跨多条路径） */
export function pathsToLineSegments(paths, radius, alt = 0) {
  const pos = []
  const v1 = new THREE.Vector3()
  const v2 = new THREE.Vector3()
  for (const path of paths) {
    for (let i = 0; i < path.length - 1; i++) {
      toVec3(radius, path[i][0], path[i][1], alt, v1)
      toVec3(radius, path[i + 1][0], path[i + 1][1], alt, v2)
      pos.push(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z)
    }
  }
  const geo = new THREE.BufferGeometry()
  geo.setAttribute('position', new THREE.Float32BufferAttribute(pos, 3))
  return geo
}

/** 沿路径密集采样（修复经度 ±180 跨越插值问题），返回展平后的点数组 */
export function resamplePath(path, segs = 48) {
  const pts = []
  let prevLng = null
  for (let i = 0; i < path.length; i++) {
    let [lat, lng] = path[i]
    if (prevLng !== null && Math.abs(lng - prevLng) > 180) {
      lng = lng + (lng < prevLng ? 360 : -360) // 短弧方向
    }
    prevLng = lng
    pts.push([lat, lng])
  }
  const out = []
  for (let i = 0; i < pts.length - 1; i++) {
    const [la1, ln1] = pts[i]
    const [la2, ln2] = pts[i + 1]
    for (let s = 0; s < segs; s++) {
      const t = s / segs
      out.push([la1 + (la2 - la1) * t, ln1 + (ln2 - ln1) * t])
    }
  }
  out.push(pts[pts.length - 1])
  return out
}

/**
 * 软圆点粒子层（自定义着色器，支持逐点尺寸/颜色/相位闪烁）
 * points: [{ lat, lng, alt?, size, color: '#hex', phase? }]
 */
export function makeSoftPoints(radius, points, opts = {}) {
  const {
    opacity = 1, pulse = 0, pulseFreq = 2.2, additive = true,
    sizeScale = 1, baseSize = 5
  } = opts

  const n = points.length
  const pos = new Float32Array(n * 3)
  const col = new Float32Array(n * 3)
  const size = new Float32Array(n)
  const phase = new Float32Array(n)
  const v = new THREE.Vector3()
  const c = new THREE.Color()

  points.forEach((p, i) => {
    toVec3(radius, p.lat, p.lng, p.alt ?? 0.004, v)
    pos.set([v.x, v.y, v.z], i * 3)
    c.set(p.color ?? opts.color ?? '#ffffff')
    col.set([c.r, c.g, c.b], i * 3)
    size[i] = (p.size ?? 1) * baseSize * sizeScale
    phase[i] = p.phase ?? Math.random() * Math.PI * 2
  })

  const geo = new THREE.BufferGeometry()
  geo.setAttribute('position', new THREE.BufferAttribute(pos, 3))
  geo.setAttribute('aColor', new THREE.BufferAttribute(col, 3))
  geo.setAttribute('aSize', new THREE.BufferAttribute(size, 1))
  geo.setAttribute('aPhase', new THREE.BufferAttribute(phase, 1))

  const material = new THREE.ShaderMaterial({
    uniforms: {
      uTime: { value: 0 },
      uPulse: { value: pulse },
      uFreq: { value: pulseFreq },
      uOpacity: { value: opacity }
    },
    vertexShader: `
      attribute vec3 aColor;
      attribute float aSize;
      attribute float aPhase;
      uniform float uTime;
      uniform float uPulse;
      uniform float uFreq;
      varying vec3 vColor;
      varying float vTw;
      void main() {
        vColor = aColor;
        vTw = 1.0 + uPulse * sin(uTime * uFreq + aPhase);
        vec4 mv = modelViewMatrix * vec4(position, 1.0);
        gl_PointSize = aSize * vTw * (160.0 / -mv.z);
        gl_Position = projectionMatrix * mv;
      }
    `,
    fragmentShader: `
      uniform float uOpacity;
      varying vec3 vColor;
      varying float vTw;
      void main() {
        vec2 c = gl_PointCoord - 0.5;
        float d = length(c);
        float a = smoothstep(0.5, 0.1, d) * uOpacity * clamp(vTw, 0.0, 2.0);
        if (a < 0.01) discard;
        gl_FragColor = vec4(vColor, a);
      }
    `,
    transparent: true,
    depthWrite: false,
    blending: additive ? THREE.AdditiveBlending : THREE.NormalBlending
  })

  const pointsObj = new THREE.Points(geo, material)
  pointsObj.frustumCulled = false

  return {
    object: pointsObj,
    uniforms: material.uniforms,
    setColor(i, hex) {
      c.set(hex)
      col.set([c.r, c.g, c.b], i * 3)
      geo.attributes.aColor.needsUpdate = true
    },
    update(t) { material.uniforms.uTime.value = t },
    dispose() { geo.dispose(); material.dispose() }
  }
}

/**
 * 沿多条路径流动的粒子（洋流/船舶等）
 * paths: [[lat,lng],...]；每条路径粒子数按长度分配
 */
export function makeFlowParticles(radius, paths, opts = {}) {
  const {
    count = 140, size = 3.2, color = '#4fc3ff', alt = 0.006,
    speed = 0.06, opacity = 0.9
  } = opts

  const sampled = paths.map(p => resamplePath(p, 24))
  const lens = sampled.map(pts => {
    let L = 0
    for (let i = 1; i < pts.length; i++) {
      L += Math.hypot(pts[i][0] - pts[i - 1][0], pts[i][1] - pts[i - 1][1])
    }
    return Math.max(L, 1)
  })
  const total = lens.reduce((a, b) => a + b, 0)

  // 粒子分配：按路径长度占比
  const particles = []
  for (let i = 0; i < sampled.length; i++) {
    const num = Math.max(2, Math.round(count * lens[i] / total))
    for (let j = 0; j < num; j++) {
      particles.push({
        path: i,
        t: Math.random(),
        v: speed * (0.7 + Math.random() * 0.6),
        off: (Math.random() - 0.5) * 0.6
      })
    }
  }

  const pos = new Float32Array(particles.length * 3)
  const col = new Float32Array(particles.length * 3)
  const sizeArr = new Float32Array(particles.length)
  const phase = new Float32Array(particles.length)
  const c = new THREE.Color(color)

  const geo = new THREE.BufferGeometry()
  geo.setAttribute('position', new THREE.BufferAttribute(pos, 3))
  geo.setAttribute('aColor', new THREE.BufferAttribute(col, 3))
  geo.setAttribute('aSize', new THREE.BufferAttribute(sizeArr, 1))
  geo.setAttribute('aPhase', new THREE.BufferAttribute(phase, 1))

  const material = new THREE.ShaderMaterial({
    uniforms: { uTime: { value: 0 }, uOpacity: { value: opacity } },
    vertexShader: `
      attribute vec3 aColor;
      attribute float aSize;
      attribute float aPhase;
      varying vec3 vColor;
      void main() {
        vColor = aColor;
        vec4 mv = modelViewMatrix * vec4(position, 1.0);
        gl_PointSize = aSize * (160.0 / -mv.z);
        gl_Position = projectionMatrix * mv;
      }
    `,
    fragmentShader: `
      uniform float uOpacity;
      varying vec3 vColor;
      void main() {
        vec2 cc = gl_PointCoord - 0.5;
        float a = smoothstep(0.5, 0.12, length(cc)) * uOpacity;
        if (a < 0.01) discard;
        gl_FragColor = vec4(vColor, a);
      }
    `,
    transparent: true,
    depthWrite: false,
    blending: THREE.AdditiveBlending
  })

  const pointsObj = new THREE.Points(geo, material)
  pointsObj.frustumCulled = false
  const v = new THREE.Vector3()

  function sample(pts, t) {
    const i = Math.min(pts.length - 1, Math.max(0, Math.floor(t * (pts.length - 1))))
    return pts[i]
  }

  let elapsed = 0
  function update(dt, t) {
    elapsed += dt
    material.uniforms.uTime.value = t
    for (let k = 0; k < particles.length; k++) {
      const P = particles[k]
      P.t = (P.t + P.v * dt) % 1
      const pts = sampled[P.path]
      const idx = (P.t * (pts.length - 1))
      const i0 = Math.floor(idx)
      const i1 = Math.min(pts.length - 1, i0 + 1)
      const f = idx - i0
      let la = pts[i0][0] + (pts[i1][0] - pts[i0][0]) * f
      let ln = pts[i0][1] + (pts[i1][1] - pts[i0][1]) * f
      // 垂直路径的横向偏移
      const la2 = la + P.off
      toVec3(radius, la2, ln, alt, v)
      pos.set([v.x, v.y, v.z], k * 3)
    }
    geo.attributes.position.needsUpdate = true
  }

  return { object: pointsObj, update, dispose() { geo.dispose(); material.dispose() } }
}

/** 文本精灵（Canvas 2D 文字 → Sprite） */
export function makeTextSprite(text, opts = {}) {
  const {
    color = '#ffffff', fontSize = 42, padding = 8,
    fontFamily = '"PingFang SC","Microsoft YaHei",sans-serif'
  } = opts
  const canvas = document.createElement('canvas')
  const ctx = canvas.getContext('2d')
  const font = `${fontSize}px ${fontFamily}`
  ctx.font = font
  const w = Math.ceil(ctx.measureText(text).width) + padding * 2
  const h = fontSize + padding * 2
  canvas.width = w
  canvas.height = h
  ctx.font = font
  ctx.textBaseline = 'middle'
  ctx.shadowColor = 'rgba(0,0,0,0.85)'
  ctx.shadowBlur = 6
  ctx.fillStyle = color
  ctx.fillText(text, padding, h / 2)

  const texture = new THREE.CanvasTexture(canvas)
  texture.colorSpace = THREE.SRGBColorSpace
  const material = new THREE.SpriteMaterial({
    map: texture, transparent: true, depthWrite: false, opacity: 0.95
  })
  const sprite = new THREE.Sprite(material)
  const scale = 0.055 * (opts.scale ?? 1)
  sprite.scale.set(scale * w / fontSize, scale, 1)
  sprite.userData.dispose = () => { texture.dispose(); material.dispose() }
  return sprite
}

/** 径向渐变光晕纹理（太阳/光斑用） */
export function makeGlowTexture(inner = 'rgba(255,255,255,1)', outer = 'rgba(255,255,255,0)', size = 128) {
  const canvas = document.createElement('canvas')
  canvas.width = canvas.height = size
  const ctx = canvas.getContext('2d')
  const g = ctx.createRadialGradient(size / 2, size / 2, 0, size / 2, size / 2, size / 2)
  g.addColorStop(0, inner)
  g.addColorStop(0.35, inner.replace(/[\d.]+\)$/, '0.55)'))
  g.addColorStop(1, outer)
  ctx.fillStyle = g
  ctx.fillRect(0, 0, size, size)
  const tex = new THREE.CanvasTexture(canvas)
  tex.colorSpace = THREE.SRGBColorSpace
  return tex
}

/* ---------- JS 侧 value noise（Canvas 程序化纹理用） ---------- */

function hash2(x, y, seed = 0) {
  let h = x * 374761393 + y * 668265263 + seed * 1442695040888963407
  h = (h ^ (h >> 13)) * 1274126177
  return ((h ^ (h >> 16)) >>> 0) / 4294967295
}

export function noise2(x, y, seed = 0) {
  const ix = Math.floor(x), iy = Math.floor(y)
  let fx = x - ix, fy = y - iy
  fx = fx * fx * (3 - 2 * fx)
  fy = fy * fy * (3 - 2 * fy)
  const a = hash2(ix, iy, seed), b = hash2(ix + 1, iy, seed)
  const c = hash2(ix, iy + 1, seed), d = hash2(ix + 1, iy + 1, seed)
  return a + (b - a) * fx + (c - a) * fy + (a - b - c + d) * fx * fy
}

export function fbm2(x, y, oct = 4, seed = 0) {
  let v = 0, amp = 0.5, f = 1
  for (let i = 0; i < oct; i++) {
    v += amp * noise2(x * f, y * f, seed + i * 7)
    f *= 2.03
    amp *= 0.5
  }
  return v
}

/** 加载图片为 HTMLImageElement */
export function loadImage(src) {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.crossOrigin = 'anonymous'
    img.onload = () => resolve(img)
    img.onerror = reject
    img.src = src
  })
}

/** 在球面上生成平行纬线圈 / 经线圈的 LineSegments 几何 */
export function buildGraticuleGeometry(radius, {
  latStep = 15, lngStep = 15, latRange = [-85, 85], lngRange = [-180, 179.9],
  alt = 0.002, latLines = true, lngLines = true
} = {}) {
  const pos = []
  const v = new THREE.Vector3()
  const push = (la, ln) => { toVec3(radius, la, ln, alt, v); pos.push(v.x, v.y, v.z) }
  if (latLines) {
    for (let lat = latRange[0]; lat <= latRange[1]; lat += latStep) {
      for (let lng = -180; lng < 180; lng += 4) {
        push(lat, lng); push(lat, Math.min(lng + 4, 179.99))
      }
    }
  }
  if (lngLines) {
    for (let lng = lngRange[0]; lng <= lngRange[1]; lng += lngStep) {
      for (let lat = latRange[0]; lat < latRange[1]; lat += 4) {
        push(lat, lng); push(lat + 4, lng)
      }
    }
  }
  const geo = new THREE.BufferGeometry()
  geo.setAttribute('position', new THREE.Float32BufferAttribute(pos, 3))
  return geo
}

/** GLSL 噪声片段（fbm），供各 ShaderMaterial 拼接 */
export const GLSL_NOISE = `
float ghash(vec2 p){ return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123); }
float gnoise(vec2 p){
  vec2 i = floor(p); vec2 f = fract(p);
  f = f * f * (3.0 - 2.0 * f);
  return mix(mix(ghash(i), ghash(i + vec2(1.0, 0.0)), f.x),
             mix(ghash(i + vec2(0.0, 1.0)), ghash(i + vec2(1.0, 1.0)), f.x), f.y);
}
float gfbm(vec2 p){
  float v = 0.0; float a = 0.5;
  for (int i = 0; i < 4; i++) { v += a * gnoise(p); p *= 2.13; a *= 0.5; }
  return v;
}
`
