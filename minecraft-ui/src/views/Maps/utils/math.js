export const PI = Math.PI
export const HALF_PI = PI / 2
export const TWO_PI = PI * 2

export function degToRad(deg) {
  return deg * PI / 180
}

export function radToDeg(rad) {
  return rad * 180 / PI
}

export function latLngToVector3(lat, lng, radius = 1) {
  const phi = (90 - lat) * PI / 180
  const theta = (lng + 180) * PI / 180
  const x = -radius * Math.sin(phi) * Math.cos(theta)
  const y = radius * Math.cos(phi)
  const z = radius * Math.sin(phi) * Math.sin(theta)
  return { x, y, z }
}

export function vector3ToLatLng(x, y, z) {
  const radius = Math.sqrt(x * x + y * y + z * z)
  const phi = Math.acos(y / radius)
  const theta = Math.atan2(z, -x)
  const lat = 90 - radToDeg(phi)
  const lng = radToDeg(theta) - 180
  return { lat, lng }
}

export function createQuaternion() {
  return { x: 0, y: 0, z: 0, w: 1 }
}

export function quaternionFromAxisAngle(axis, angle) {
  const halfAngle = angle / 2
  const s = Math.sin(halfAngle)
  return {
    x: axis.x * s,
    y: axis.y * s,
    z: axis.z * s,
    w: Math.cos(halfAngle)
  }
}

export function quaternionMultiply(q1, q2) {
  return {
    x: q1.w * q2.x + q1.x * q2.w + q1.y * q2.z - q1.z * q2.y,
    y: q1.w * q2.y - q1.x * q2.z + q1.y * q2.w + q1.z * q2.x,
    z: q1.w * q2.z + q1.x * q2.y - q1.y * q2.x + q1.z * q2.w,
    w: q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z
  }
}

export function quaternionNormalize(q) {
  const length = Math.sqrt(q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w)
  if (length === 0) {
    return { x: 0, y: 0, z: 0, w: 1 }
  }
  return {
    x: q.x / length,
    y: q.y / length,
    z: q.z / length,
    w: q.w / length
  }
}

export function quaternionSlerp(q1, q2, t) {
  let dot = q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w
  if (dot < 0) {
    q2 = { x: -q2.x, y: -q2.y, z: -q2.z, w: -q2.w }
    dot = -dot
  }
  if (dot > 0.9995) {
    return quaternionNormalize({
      x: q1.x + (q2.x - q1.x) * t,
      y: q1.y + (q2.y - q1.y) * t,
      z: q1.z + (q2.z - q1.z) * t,
      w: q1.w + (q2.w - q1.w) * t
    })
  }
  const theta0 = Math.acos(dot)
  const theta = theta0 * t
  const sinTheta = Math.sin(theta)
  const sinTheta0 = Math.sin(theta0)
  const s1 = Math.cos(theta) - dot * sinTheta / sinTheta0
  const s2 = sinTheta / sinTheta0
  return {
    x: s1 * q1.x + s2 * q2.x,
    y: s1 * q1.y + s2 * q2.y,
    z: s1 * q1.z + s2 * q2.z,
    w: s1 * q1.w + s2 * q2.w
  }
}

export function quaternionToMatrix4(q) {
  const { x, y, z, w } = q
  const x2 = x * x
  const y2 = y * y
  const z2 = z * z
  const xy = x * y
  const xz = x * z
  const yz = y * z
  const wx = w * x
  const wy = w * y
  const wz = w * z
  return [
    1 - 2 * (y2 + z2), 2 * (xy + wz), 2 * (xz - wy), 0,
    2 * (xy - wz), 1 - 2 * (x2 + z2), 2 * (yz + wx), 0,
    2 * (xz + wy), 2 * (yz - wx), 1 - 2 * (x2 + y2), 0,
    0, 0, 0, 1
  ]
}

export function createPerspectiveMatrix(fov, aspect, near, far) {
  const f = 1.0 / Math.tan(fov / 2)
  const nf = 1 / (near - far)
  return [
    f / aspect, 0, 0, 0,
    0, f, 0, 0,
    0, 0, (far + near) * nf, -1,
    0, 0, 2 * far * near * nf, 0
  ]
}

export function createLookAtMatrix(eye, target, up) {
  const zAxis = normalize({
    x: eye.x - target.x,
    y: eye.y - target.y,
    z: eye.z - target.z
  })
  const xAxis = normalize(cross(up, zAxis))
  const yAxis = cross(zAxis, xAxis)
  return [
    xAxis.x, yAxis.x, zAxis.x, 0,
    xAxis.y, yAxis.y, zAxis.y, 0,
    xAxis.z, yAxis.z, zAxis.z, 0,
    -dot(xAxis, eye), -dot(yAxis, eye), -dot(zAxis, eye), 1
  ]
}

export function multiplyMatrices(a, b) {
  const result = new Array(16).fill(0)
  for (let i = 0; i < 4; i++) {
    for (let r = 0; r < 4; r++) {
      let sum = 0
      for (let k = 0; k < 4; k++) {
        sum += a[k * 4 + r] * b[i * 4 + k]
      }
      result[i * 4 + r] = sum
    }
  }
  return result
}

export function transformPointByMatrix(matrix, point) {
  const x = point.x
  const y = point.y
  const z = point.z
  const w = point.w !== undefined ? point.w : 1
  return {
    x: matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12] * w,
    y: matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13] * w,
    z: matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14] * w,
    w: matrix[3] * x + matrix[7] * y + matrix[11] * z + matrix[15] * w
  }
}

export function normalize(v) {
  const length = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
  if (length === 0) return { x: 0, y: 0, z: 0 }
  return { x: v.x / length, y: v.y / length, z: v.z / length }
}

export function cross(a, b) {
  return {
    x: a.y * b.z - a.z * b.y,
    y: a.z * b.x - a.x * b.z,
    z: a.x * b.y - a.y * b.x
  }
}

export function dot(a, b) {
  return a.x * b.x + a.y * b.y + a.z * b.z
}

export function lerp(a, b, t) {
  return a + (b - a) * t
}

export function clamp(value, min, max) {
  return Math.min(Math.max(value, min), max)
}

export function smoothStep(edge0, edge1, x) {
  const t = clamp((x - edge0) / (edge1 - edge0), 0, 1)
  return t * t * (3 - 2 * t)
}

export function easeInOutCubic(t) {
  return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2
}

export function easeOutQuart(t) {
  return 1 - Math.pow(1 - t, 4)
}

export function generateUVSphere(radius, widthSegments, heightSegments) {
  const positions = []
  const normals = []
  const uvs = []
  const indices = []
  for (let y = 0; y <= heightSegments; y++) {
    const v = y / heightSegments
    const theta = v * PI
    const sinTheta = Math.sin(theta)
    const cosTheta = Math.cos(theta)
    for (let x = 0; x <= widthSegments; x++) {
      const u = x / widthSegments
      const phi = u * TWO_PI
      const sinPhi = Math.sin(phi)
      const cosPhi = Math.cos(phi)
      const px = -cosPhi * sinTheta
      const py = cosTheta
      const pz = sinPhi * sinTheta
      positions.push(px * radius, py * radius, pz * radius)
      normals.push(px, py, pz)
      uvs.push(u, v)
    }
  }
  for (let y = 0; y < heightSegments; y++) {
    for (let x = 0; x < widthSegments; x++) {
      const a = y * (widthSegments + 1) + x
      const b = a + widthSegments + 1
      indices.push(a, b, a + 1)
      indices.push(b, b + 1, a + 1)
    }
  }
  return { positions, normals, uvs, indices }
}

export function raySphereIntersection(rayOrigin, rayDir, sphereCenter, sphereRadius) {
  const oc = {
    x: rayOrigin.x - sphereCenter.x,
    y: rayOrigin.y - sphereCenter.y,
    z: rayOrigin.z - sphereCenter.z
  }
  const a = dot(rayDir, rayDir)
  const b = 2 * dot(oc, rayDir)
  const c = dot(oc, oc) - sphereRadius * sphereRadius
  const discriminant = b * b - 4 * a * c
  if (discriminant < 0) return null
  const sqrtD = Math.sqrt(discriminant)
  const t1 = (-b - sqrtD) / (2 * a)
  const t2 = (-b + sqrtD) / (2 * a)
  const t = t1 > 0 ? t1 : (t2 > 0 ? t2 : null)
  if (t === null) return null
  return {
    distance: t,
    point: {
      x: rayOrigin.x + rayDir.x * t,
      y: rayOrigin.y + rayDir.y * t,
      z: rayOrigin.z + rayDir.z * t
    }
  }
}

export function unprojectPoint(screenX, screenY, width, height, viewProjectionMatrix) {
  const x = (2 * screenX) / width - 1
  const y = 1 - (2 * screenY) / height
  const invMatrix = inverseMatrix4(viewProjectionMatrix)
  const nearPoint = transformPointByMatrix(invMatrix, { x, y, z: -1, w: 1 })
  const farPoint = transformPointByMatrix(invMatrix, { x, y, z: 1, w: 1 })
  const nearDiv = { x: nearPoint.x / nearPoint.w, y: nearPoint.y / nearPoint.w, z: nearPoint.z / nearPoint.w }
  const farDiv = { x: farPoint.x / farPoint.w, y: farPoint.y / farPoint.w, z: farPoint.z / farPoint.w }
  const direction = normalize({
    x: farDiv.x - nearDiv.x,
    y: farDiv.y - nearDiv.y,
    z: farDiv.z - nearDiv.z
  })
  return { origin: nearDiv, direction }
}

function inverseMatrix4(m) {
  const inv = new Array(16)
  inv[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6] * m[15] +
           m[9] * m[7] * m[14] + m[13] * m[6] * m[11] - m[13] * m[7] * m[10]
  inv[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6] * m[15] -
           m[8] * m[7] * m[14] - m[12] * m[6] * m[11] + m[12] * m[7] * m[10]
  inv[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5] * m[15] +
           m[8] * m[7] * m[13] + m[12] * m[5] * m[11] - m[12] * m[7] * m[9]
  inv[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5] * m[14] -
            m[8] * m[6] * m[13] - m[12] * m[5] * m[10] + m[12] * m[6] * m[9]
  inv[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2] * m[15] -
           m[9] * m[3] * m[14] - m[13] * m[2] * m[11] + m[13] * m[3] * m[10]
  inv[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2] * m[15] +
           m[8] * m[3] * m[14] + m[12] * m[2] * m[11] - m[12] * m[3] * m[10]
  inv[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1] * m[15] -
           m[8] * m[3] * m[13] - m[12] * m[1] * m[11] + m[12] * m[3] * m[9]
  inv[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1] * m[14] +
            m[8] * m[2] * m[13] + m[12] * m[1] * m[10] - m[12] * m[2] * m[9]
  inv[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2] * m[15] +
           m[5] * m[3] * m[14] + m[13] * m[2] * m[7] - m[13] * m[3] * m[6]
  inv[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2] * m[15] -
           m[4] * m[3] * m[14] - m[12] * m[2] * m[7] + m[12] * m[3] * m[6]
  inv[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1] * m[15] +
            m[4] * m[3] * m[13] + m[12] * m[1] * m[7] - m[12] * m[3] * m[5]
  inv[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1] * m[14] -
            m[4] * m[2] * m[13] - m[12] * m[1] * m[6] + m[12] * m[2] * m[5]
  inv[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2] * m[11] -
           m[5] * m[3] * m[10] - m[9] * m[2] * m[7] + m[9] * m[3] * m[6]
  inv[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2] * m[11] +
           m[4] * m[3] * m[10] + m[8] * m[2] * m[7] - m[8] * m[3] * m[6]
  inv[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1] * m[11] -
            m[4] * m[3] * m[9] - m[8] * m[1] * m[7] + m[8] * m[3] * m[5]
  inv[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1] * m[10] +
            m[4] * m[2] * m[9] + m[8] * m[1] * m[6] - m[8] * m[2] * m[5]
  let det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12]
  if (det === 0) return m
  det = 1.0 / det
  for (let i = 0; i < 16; i++) {
    inv[i] *= det
  }
  return inv
}
