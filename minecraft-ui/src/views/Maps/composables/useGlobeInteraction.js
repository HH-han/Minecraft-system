import { ref, reactive, computed, onUnmounted } from 'vue'
import {
  createQuaternion,
  quaternionFromAxisAngle,
  quaternionMultiply,
  quaternionNormalize,
  quaternionSlerp,
  quaternionToMatrix4,
  createPerspectiveMatrix,
  createLookAtMatrix,
  multiplyMatrices,
  latLngToVector3,
  vector3ToLatLng,
  normalize,
  lerp,
  clamp,
  easeInOutCubic,
  raySphereIntersection,
  unprojectPoint
} from '../utils/math.js'
import { findNearestContinent } from '../data/continents.js'
import { findNearestCountry } from '../data/countries.js'

export function useGlobeInteraction(canvasRef) {
  const rotation = reactive({ x: 0.3, y: 0 })
  const targetRotation = reactive({ x: 0.3, y: 0 })
  const zoom = ref(3)
  const targetZoom = ref(3)
  const panOffset = reactive({ x: 0, y: 0 })
  const targetPanOffset = reactive({ x: 0, y: 0 })
  const isDragging = ref(false)
  const isRightDragging = ref(false)
  const autoRotate = ref(true)
  const zoomLevel = computed(() => {
    const base = 1
    const maxLevel = 5
    const z = clamp(zoom.value, 1.5, 10)
    const level = (10 - z) / (10 - 1.5) * (maxLevel - base) + base
    return level
  })
  const canClickCountries = computed(() => zoomLevel.value > 2.5)
  const state = reactive({
    selectedContinent: null,
    selectedCountry: null,
    hoveredCountry: null,
    isAnimating: false
  })

  let _lastPos = { x: 0, y: 0 }
  let _velocity = { x: 0, y: 0 }
  let _zoomVelocity = 0
  let _animationFrameId = null
  let _autoRotateSpeed = 0.002

  function handleMouseDown(e) {
    if (e.button === 0) {
      isDragging.value = true
      autoRotate.value = false
      _lastPos = { x: e.clientX, y: e.clientY }
      _velocity = { x: 0, y: 0 }
    } else if (e.button === 2) {
      isRightDragging.value = true
      _lastPos = { x: e.clientX, y: e.clientY }
    }
  }

  function handleMouseMove(e) {
    if (isDragging.value) {
      const dx = e.clientX - _lastPos.x
      const dy = e.clientY - _lastPos.y
      const sensitivity = 0.005
      targetRotation.y -= dx * sensitivity
      targetRotation.x -= dy * sensitivity
      targetRotation.x = clamp(targetRotation.x, -Math.PI / 2 + 0.1, Math.PI / 2 - 0.1)
      _velocity = { x: dx * sensitivity, y: dy * sensitivity }
      _lastPos = { x: e.clientX, y: e.clientY }
    } else if (isRightDragging.value) {
      const dx = e.clientX - _lastPos.x
      const dy = e.clientY - _lastPos.y
      targetPanOffset.x += dx * 0.01
      targetPanOffset.y += dy * 0.01
      _lastPos = { x: e.clientX, y: e.clientY }
    }
  }

  function handleMouseUp(e) {
    if (e.button === 0 && isDragging.value) {
      isDragging.value = false
      if (Math.abs(_velocity.x) < 0.001 && Math.abs(_velocity.y) < 0.001) {
        handleClick(e)
      }
    }
    if (e.button === 2) {
      isRightDragging.value = false
    }
  }

  function handleWheel(e) {
    e.preventDefault()
    const delta = e.deltaY > 0 ? 0.3 : -0.3
    targetZoom.value = clamp(targetZoom.value + delta, 1.5, 10)
    autoRotate.value = false
  }

  function handleClick(e) {
    const canvas = canvasRef.value
    if (!canvas) return
    const rect = canvas.getBoundingClientRect()
    const x = e.clientX - rect.left
    const y = e.clientY - rect.top
    const result = raycast(x, y, canvas.clientWidth, canvas.clientHeight)
    if (result) {
      const { lat, lng } = vector3ToLatLng(result.point.x, result.point.y, result.point.z)
      if (canClickCountries.value) {
        const country = findNearestCountry(lat, lng, 2000)
        if (country) {
          state.selectedCountry = country
          state.selectedContinent = null
          return
        }
      }
      const continent = findNearestContinent(lat, lng)
      if (continent) {
        state.selectedContinent = continent
        state.selectedCountry = null
        focusOnContinent(continent)
      }
    }
  }

  function raycast(screenX, screenY, width, height) {
    const cameraPos = { x: 0, y: 0, z: zoom.value }
    const aspect = width / height
    const fov = Math.PI / 4
    const projectionMatrix = createPerspectiveMatrix(fov, aspect, 0.1, 100)
    const viewMatrix = createLookAtMatrix(cameraPos, { x: 0, y: 0, z: 0 }, { x: 0, y: 1, z: 0 })
    const rotMatrix = quaternionToMatrix4(getCurrentQuaternion())
    const modelView = multiplyMatrices(viewMatrix, rotMatrix)
    const vpMatrix = multiplyMatrices(projectionMatrix, modelView)
    const ray = unprojectPoint(screenX, screenY, width, height, vpMatrix)
    const hit = raySphereIntersection(
      cameraPos,
      ray.direction,
      { x: 0, y: 0, z: 0 },
      1
    )
    if (hit) {
      const invRot = getCurrentQuaternion()
      const invQuat = { x: -invRot.x, y: -invRot.y, z: -invRot.z, w: invRot.w }
      const invMatrix = quaternionToMatrix4(invQuat)
      const localPoint = {
        x: invMatrix[0] * hit.point.x + invMatrix[4] * hit.point.y + invMatrix[8] * hit.point.z + invMatrix[12],
        y: invMatrix[1] * hit.point.x + invMatrix[5] * hit.point.y + invMatrix[9] * hit.point.z + invMatrix[13],
        z: invMatrix[2] * hit.point.x + invMatrix[6] * hit.point.y + invMatrix[10] * hit.point.z + invMatrix[14]
      }
      return { point: localPoint, distance: hit.distance }
    }
    return null
  }

  function getCurrentQuaternion() {
    const qx = quaternionFromAxisAngle({ x: 1, y: 0, z: 0 }, rotation.x)
    const qy = quaternionFromAxisAngle({ x: 0, y: 1, z: 0 }, rotation.y)
    return quaternionNormalize(quaternionMultiply(qy, qx))
  }

  function focusOnContinent(continent) {
    state.isAnimating = true
    const startRot = { x: targetRotation.x, y: targetRotation.y }
    const startZoom = targetZoom.value
    const targetLat = continent.centerLat
    const targetLng = continent.centerLng
    const targetRotY = -targetLng * Math.PI / 180
    const targetRotX = targetLat * Math.PI / 180
    const endZoom = 2.2
    const duration = 1500
    const startTime = performance.now()
    function animate(currentTime) {
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / duration, 1)
      const eased = easeInOutCubic(progress)
      targetRotation.x = lerp(startRot.x, targetRotX, eased)
      targetRotation.y = lerp(startRot.y, targetRotY, eased)
      targetZoom.value = lerp(startZoom, endZoom, eased)
      if (progress < 1) {
        _animationFrameId = requestAnimationFrame(animate)
      } else {
        state.isAnimating = false
      }
    }
    _animationFrameId = requestAnimationFrame(animate)
  }

  function focusOnCountry(country) {
    state.isAnimating = true
    const startRot = { x: targetRotation.x, y: targetRotation.y }
    const startZoom = targetZoom.value
    const targetRotY = -country.lng * Math.PI / 180
    const targetRotX = country.lat * Math.PI / 180
    const endZoom = 1.8
    const duration = 1200
    const startTime = performance.now()
    function animate(currentTime) {
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / duration, 1)
      const eased = easeInOutCubic(progress)
      targetRotation.x = lerp(startRot.x, targetRotX, eased)
      targetRotation.y = lerp(startRot.y, targetRotY, eased)
      targetZoom.value = lerp(startZoom, endZoom, eased)
      if (progress < 1) {
        _animationFrameId = requestAnimationFrame(animate)
      } else {
        state.isAnimating = false
      }
    }
    _animationFrameId = requestAnimationFrame(animate)
  }

  function zoomIn() {
    targetZoom.value = clamp(targetZoom.value - 0.8, 1.5, 10)
    autoRotate.value = false
  }

  function zoomOut() {
    targetZoom.value = clamp(targetZoom.value + 0.8, 1.5, 10)
    autoRotate.value = false
  }

  function resetView() {
    state.isAnimating = true
    const startRot = { x: targetRotation.x, y: targetRotation.y }
    const startZoom = targetZoom.value
    const startPan = { x: targetPanOffset.x, y: targetPanOffset.y }
    const endRot = { x: 0.3, y: 0 }
    const endZoom = 3
    const endPan = { x: 0, y: 0 }
    const duration = 1000
    const startTime = performance.now()
    function animate(currentTime) {
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / duration, 1)
      const eased = easeInOutCubic(progress)
      targetRotation.x = lerp(startRot.x, endRot.x, eased)
      targetRotation.y = lerp(startRot.y, endRot.y, eased)
      targetZoom.value = lerp(startZoom, endZoom, eased)
      targetPanOffset.x = lerp(startPan.x, endPan.x, eased)
      targetPanOffset.y = lerp(startPan.y, endPan.y, eased)
      if (progress < 1) {
        _animationFrameId = requestAnimationFrame(animate)
      } else {
        state.isAnimating = false
        state.selectedContinent = null
        state.selectedCountry = null
        autoRotate.value = true
      }
    }
    _animationFrameId = requestAnimationFrame(animate)
  }

  function update() {
    const smoothness = 0.08
    rotation.x += (targetRotation.x - rotation.x) * smoothness
    rotation.y += (targetRotation.y - rotation.y) * smoothness
    zoom.value += (targetZoom.value - zoom.value) * smoothness
    panOffset.x += (targetPanOffset.x - panOffset.x) * smoothness
    panOffset.y += (targetPanOffset.y - panOffset.y) * smoothness
    if (autoRotate.value && !isDragging.value && !state.isAnimating) {
      targetRotation.y += _autoRotateSpeed
    }
    if (!isDragging.value && Math.abs(_velocity.x) > 0.0001) {
      targetRotation.y -= _velocity.x * 0.5
      _velocity.x *= 0.95
    }
  }

  function getViewProjectionMatrix(width, height) {
    const aspect = width / height
    const fov = Math.PI / 4
    const near = 0.1
    const far = 100
    const projectionMatrix = createPerspectiveMatrix(fov, aspect, near, far)
    const cameraPos = {
      x: panOffset.x,
      y: panOffset.y,
      z: zoom.value
    }
    const viewMatrix = createLookAtMatrix(
      cameraPos,
      { x: panOffset.x, y: panOffset.y, z: 0 },
      { x: 0, y: 1, z: 0 }
    )
    return multiplyMatrices(projectionMatrix, viewMatrix)
  }

  function getModelMatrix() {
    return quaternionToMatrix4(getCurrentQuaternion())
  }

  function getNormalMatrix(modelMatrix) {
    return [
      modelMatrix[0], modelMatrix[1], modelMatrix[2], 0,
      modelMatrix[4], modelMatrix[5], modelMatrix[6], 0,
      modelMatrix[8], modelMatrix[9], modelMatrix[10], 0,
      0, 0, 0, 1
    ]
  }

  function getCameraPosition() {
    return {
      x: panOffset.x,
      y: panOffset.y,
      z: zoom.value
    }
  }

  function setAutoRotate(value) {
    autoRotate.value = value
  }

  function closeInfoPanel() {
    state.selectedContinent = null
    state.selectedCountry = null
  }

  function setupEventListeners() {
    const canvas = canvasRef.value
    if (!canvas) return
    canvas.addEventListener('mousedown', handleMouseDown)
    window.addEventListener('mousemove', handleMouseMove)
    window.addEventListener('mouseup', handleMouseUp)
    canvas.addEventListener('wheel', handleWheel, { passive: false })
    canvas.addEventListener('contextmenu', (e) => e.preventDefault())
  }

  function removeEventListeners() {
    const canvas = canvasRef.value
    if (!canvas) return
    canvas.removeEventListener('mousedown', handleMouseDown)
    window.removeEventListener('mousemove', handleMouseMove)
    window.removeEventListener('mouseup', handleMouseUp)
    canvas.removeEventListener('wheel', handleWheel)
  }

  function cleanup() {
    removeEventListeners()
    if (_animationFrameId) {
      cancelAnimationFrame(_animationFrameId)
    }
  }

  onUnmounted(() => {
    cleanup()
  })

  return {
    rotation,
    targetRotation,
    zoom,
    targetZoom,
    zoomLevel,
    canClickCountries,
    state,
    isDragging,
    autoRotate,
    panOffset,
    setupEventListeners,
    removeEventListeners,
    update,
    getViewProjectionMatrix,
    getModelMatrix,
    getNormalMatrix,
    getCameraPosition,
    getCurrentQuaternion,
    focusOnContinent,
    focusOnCountry,
    zoomIn,
    zoomOut,
    resetView,
    setAutoRotate,
    closeInfoPanel,
    raycast,
    cleanup
  }
}
