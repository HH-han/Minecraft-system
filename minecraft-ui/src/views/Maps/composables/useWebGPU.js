import { ref, onUnmounted, shallowRef } from 'vue'
import { generateContinentGlobeData, generateCountryGlobeData, generateBoundaryLinesData } from '../utils/regionData.js'
import { generateDotQuadData, findContinentAt, generateGlowMarkerData } from '../utils/dotGlobe.js'
import { glowMarkers } from '../data/markers.js'
import { loadGeoBoundaries } from '../utils/geoBoundaries.js'

const regionVertexShader = /* wgsl */ `
struct Uniforms {
  modelViewProjectionMatrix: mat4x4<f32>,
  normalMatrix: mat4x4<f32>,
  lightDirection: vec3<f32>,
  cameraPosition: vec3<f32>,
  time: f32,
  atmosphereStrength: f32,
}

@group(0) @binding(0) var<uniform> uniforms: Uniforms;

struct VertexOutput {
  @builtin(position) position: vec4<f32>,
  @location(0) worldPos: vec3<f32>,
  @location(1) color: vec3<f32>,
}

@vertex
fn vs_main(@location(0) position: vec3<f32>, @location(1) color: vec3<f32>) -> VertexOutput {
  var output: VertexOutput;
  output.position = uniforms.modelViewProjectionMatrix * vec4<f32>(position, 1.0);
  output.worldPos = position;
  output.color = color;
  return output;
}

@fragment
fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
  let normal = normalize(input.worldPos);
  let lightDir = normalize(uniforms.lightDirection);
  let ambient = 0.35;
  let diffuse = max(dot(normal, lightDir), 0.0);
  let lighting = ambient + diffuse * 0.65;
  let finalColor = input.color * lighting;
  return vec4<f32>(finalColor, 1.0);
}
`

const lineVertexShader = /* wgsl */ `
struct Uniforms {
  modelViewProjectionMatrix: mat4x4<f32>,
  normalMatrix: mat4x4<f32>,
  lightDirection: vec3<f32>,
  cameraPosition: vec3<f32>,
  time: f32,
  atmosphereStrength: f32,
}

@group(0) @binding(0) var<uniform> uniforms: Uniforms;

struct VertexOutput {
  @builtin(position) position: vec4<f32>,
  @location(0) color: vec3<f32>,
}

@vertex
fn vs_main(@location(0) position: vec3<f32>, @location(1) color: vec3<f32>) -> VertexOutput {
  var output: VertexOutput;
  output.position = uniforms.modelViewProjectionMatrix * vec4<f32>(position, 1.0);
  output.color = color;
  return output;
}

@fragment
fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
  return vec4<f32>(input.color, 1.0);
}
`

const atmosphereVertexShader = /* wgsl */ `
struct Uniforms {
  modelViewProjectionMatrix: mat4x4<f32>,
  normalMatrix: mat4x4<f32>,
  lightDirection: vec3<f32>,
  cameraPosition: vec3<f32>,
  time: f32,
  atmosphereStrength: f32,
}

@group(0) @binding(0) var<uniform> uniforms: Uniforms;

struct VertexOutput {
  @builtin(position) position: vec4<f32>,
  @location(0) normal: vec3<f32>,
  @location(1) worldPos: vec3<f32>,
}

@vertex
fn vs_main(@location(0) position: vec3<f32>, @location(1) normal: vec3<f32>) -> VertexOutput {
  var output: VertexOutput;
  let scaledPos = position * 1.02;
  output.position = uniforms.modelViewProjectionMatrix * vec4<f32>(scaledPos, 1.0);
  output.normal = normal;
  output.worldPos = scaledPos;
  return output;
}

@fragment
fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
  let normal = normalize(input.normal);
  let viewDir = normalize(uniforms.cameraPosition - input.worldPos);
  let intensity = pow(1.0 - abs(dot(normal, viewDir)), 2.5);
  let color = vec3<f32>(0.08, 0.12, 0.18) * intensity;
  return vec4<f32>(color, intensity * 0.5);
}
`

const starsVertexShader = /* wgsl */ `
struct Uniforms {
  modelViewProjectionMatrix: mat4x4<f32>,
  normalMatrix: mat4x4<f32>,
  lightDirection: vec3<f32>,
  cameraPosition: vec3<f32>,
  time: f32,
  atmosphereStrength: f32,
}

@group(0) @binding(0) var<uniform> uniforms: Uniforms;

struct VertexOutput {
  @builtin(position) position: vec4<f32>,
  @location(0) color: vec3<f32>,
}

@vertex
fn vs_main(@location(0) position: vec3<f32>, @location(1) color: vec3<f32>) -> VertexOutput {
  var output: VertexOutput;
  output.position = uniforms.modelViewProjectionMatrix * vec4<f32>(position * 50.0, 1.0);
  output.color = color;
  return output;
}

@fragment
fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
  return vec4<f32>(input.color, 1.0);
}
`

const circleDotVertexShader = /* wgsl */ `
struct Uniforms {
  modelViewProjectionMatrix: mat4x4<f32>,
  normalMatrix: mat4x4<f32>,
  lightDirection: vec3<f32>,
  cameraPosition: vec3<f32>,
  time: f32,
  atmosphereStrength: f32,
}

@group(0) @binding(0) var<uniform> uniforms: Uniforms;

struct VertexOutput {
  @builtin(position) position: vec4<f32>,
  @location(0) uv: vec2<f32>,
  @location(1) color: vec3<f32>,
  @location(2) worldPos: vec3<f32>,
}

@vertex
fn vs_main(@location(0) position: vec3<f32>, @location(1) uv: vec2<f32>, @location(2) color: vec3<f32>) -> VertexOutput {
  var output: VertexOutput;
  output.position = uniforms.modelViewProjectionMatrix * vec4<f32>(position, 1.0);
  output.uv = uv;
  output.color = color;
  output.worldPos = position;
  return output;
}

@fragment
fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
  let d = dot(input.uv, input.uv);
  // Discard fragments outside the circle radius
  if (d > 1.0) {
    discard;
  }
  // Smooth edge for anti-aliased circle
  let alpha = 1.0 - smoothstep(0.85, 1.0, d);
  let normal = normalize(input.worldPos);
  let lightDir = normalize(uniforms.lightDirection);
  let ambient = 0.35;
  let diffuse = max(dot(normal, lightDir), 0.0);
  let lighting = ambient + diffuse * 0.65;
  let finalColor = input.color * lighting;
  return vec4<f32>(finalColor, alpha);
}
`

const glowMarkerVertexShader = /* wgsl */ `
struct Uniforms {
  modelViewProjectionMatrix: mat4x4<f32>,
  normalMatrix: mat4x4<f32>,
  lightDirection: vec3<f32>,
  cameraPosition: vec3<f32>,
  time: f32,
  atmosphereStrength: f32,
}

@group(0) @binding(0) var<uniform> uniforms: Uniforms;

struct VertexOutput {
  @builtin(position) position: vec4<f32>,
  @location(0) color: vec3<f32>,
  @location(1) worldPos: vec3<f32>,
}

@vertex
fn vs_main(@location(0) position: vec3<f32>, @location(1) color: vec3<f32>) -> VertexOutput {
  var output: VertexOutput;
  output.position = uniforms.modelViewProjectionMatrix * vec4<f32>(position, 1.0);
  output.color = color;
  output.worldPos = position;
  return output;
}

@fragment
fn fs_main(input: VertexOutput) -> @location(0) vec4<f32> {
  let viewDir = normalize(uniforms.cameraPosition - input.worldPos);
  let fresnel = 1.0 - max(0.0, dot(normalize(input.worldPos), viewDir));
  let glow = pow(fresnel, 1.5) * 0.6 + 0.4;
  let alpha = glow * 0.5; // 50% transparency base
  return vec4<f32>(input.color * 1.3, alpha);
}
`

export function useWebGPU(canvasRef) {
  const device = shallowRef(null)
  const context = shallowRef(null)
  const format = ref('')
  const isSupported = ref(false)
  const isReady = ref(false)
  const errorMessage = ref('')

  let _device = null
  let _context = null
  let _format = ''
  let _regionPipeline = null
  let _linePipeline = null
  let _atmospherePipeline = null
  let _starsPipeline = null
  let _glowMarkerPipeline = null
  let _regionUniformBuffer = null
  let _lineUniformBuffer = null
  let _atmosphereUniformBuffer = null
  let _starsUniformBuffer = null
  let _glowMarkerUniformBuffer = null
  let _regionBindGroup = null
  let _lineBindGroup = null
  let _atmosphereBindGroup = null
  let _starsBindGroup = null
  let _glowMarkerBindGroup = null
  
  let _continentBuffer = null
  let _continentIndexBuffer = null
  let _continentIndexCount = 0
  let _continentLineBuffer = null
  let _continentLineCount = 0
  
  let _countryBuffer = null
  let _countryIndexBuffer = null
  let _countryIndexCount = 0
  let _countryLineBuffer = null
  let _countryLineCount = 0
  
  // Dot globe
  let _dotGlobeBuffer = null
  let _dotGlobeIndexBuffer = null
  let _dotGlobeIndexCount = 0
  let _dotGlobePipeline = null
  let _dotGlobeBindGroup = null
  let _dotGlobeUniformBuffer = null
  
  // Glow markers
  let _glowMarkerBuffer = null
  let _glowMarkerIndexBuffer = null
  let _glowMarkerIndexCount = 0
  let _glowMarkerMeta = []
  
  let _atmosphereVertexBuffer = null
  let _atmosphereIndexBuffer = null
  let _atmosphereIndexCount = 0
  let _starsVertexBuffer = null
  let _starsCount = 0
  let _depthTexture = null
  let _depthTextureView = null
  
  let _currentLevel = ref('continent')

  async function initWebGPU() {
    if (!navigator.gpu) {
      isSupported.value = false
      errorMessage.value = '您的浏览器不支持 WebGPU，请使用最新版 Chrome 或 Edge 浏览器。'
      console.error('[WebGPU] navigator.gpu not found')
      return false
    }
    const canvas = canvasRef.value
    if (!canvas) {
      console.error('[WebGPU] canvas ref is null')
      return false
    }
    try {
      console.log('[WebGPU] Requesting GPU adapter...')
      const adapter = await navigator.gpu.requestAdapter()
      if (!adapter) {
        errorMessage.value = '无法获取 GPU 适配器。'
        console.error('[WebGPU] Failed to get GPU adapter')
        return false
      }
      console.log('[WebGPU] Requesting GPU device...')
      _device = await adapter.requestDevice()
      device.value = _device
      _context = canvas.getContext('webgpu')
      context.value = _context
      _format = navigator.gpu.getPreferredCanvasFormat()
      format.value = _format
      console.log('[WebGPU] Canvas format:', _format)
      _context.configure({
        device: _device,
        format: _format,
        alphaMode: 'premultiplied'
      })
      isSupported.value = true
      console.log('[WebGPU] Initialization successful')
      return true
    } catch (e) {
      errorMessage.value = `WebGPU 初始化失败: ${e.message}`
      console.error('[WebGPU] Init error:', e)
      return false
    }
  }

  function createBuffer(data, usage) {
    const buffer = _device.createBuffer({
      size: data.byteLength,
      usage,
      mappedAtCreation: true
    })
    new data.constructor(buffer.getMappedRange()).set(data)
    buffer.unmap()
    return buffer
  }

  function createSphereBuffers(radius, widthSegments, heightSegments) {
    const positions = []
    const normals = []
    const indices = []
    for (let y = 0; y <= heightSegments; y++) {
      const v = y / heightSegments
      const theta = v * Math.PI
      const sinTheta = Math.sin(theta)
      const cosTheta = Math.cos(theta)
      for (let x = 0; x <= widthSegments; x++) {
        const u = x / widthSegments
        const phi = u * Math.PI * 2
        const sinPhi = Math.sin(phi)
        const cosPhi = Math.cos(phi)
        const px = -cosPhi * sinTheta
        const py = cosTheta
        const pz = sinPhi * sinTheta
        positions.push(px * radius, py * radius, pz * radius)
        normals.push(px, py, pz)
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
    const floatStride = 6
    const vertexData = new Float32Array(positions.length / 3 * floatStride)
    for (let i = 0; i < positions.length / 3; i++) {
      vertexData[i * floatStride] = positions[i * 3]
      vertexData[i * floatStride + 1] = positions[i * 3 + 1]
      vertexData[i * floatStride + 2] = positions[i * 3 + 2]
      vertexData[i * floatStride + 3] = normals[i * 3]
      vertexData[i * floatStride + 4] = normals[i * 3 + 1]
      vertexData[i * floatStride + 5] = normals[i * 3 + 2]
    }
    const vertexBuffer = createBuffer(vertexData, GPUBufferUsage.VERTEX)
    const indexBuffer = createBuffer(new Uint32Array(indices), GPUBufferUsage.INDEX)
    return { vertexBuffer, indexBuffer, indexCount: indices.length }
  }

  function createStars(count = 2000) {
    const data = []
    for (let i = 0; i < count; i++) {
      const theta = Math.random() * Math.PI * 2
      const phi = Math.acos(2 * Math.random() - 1)
      const r = 1
      const x = r * Math.sin(phi) * Math.cos(theta)
      const y = r * Math.sin(phi) * Math.sin(theta)
      const z = r * Math.cos(phi)
      const brightness = 0.5 + Math.random() * 0.5
      const color = [brightness, brightness, brightness * 0.95]
      data.push(x, y, z, ...color)
    }
    const buffer = createBuffer(new Float32Array(data), GPUBufferUsage.VERTEX)
    return { buffer, count }
  }

  async function initRenderPipeline(textureUrl) {
    if (!_device) return false
    try {
      console.log('[WebGPU] Loading geographic boundary data...')
      
      // Load real geographic boundaries before generating geometry
      const geoLoaded = await loadGeoBoundaries()
      if (!geoLoaded) {
        console.warn('[WebGPU] GeoBoundaries load failed, using fallback geometry')
      } else {
        console.log('[WebGPU] Geographic boundaries loaded successfully')
      }
      
      console.log('[WebGPU] Creating region globe geometry...')
      
      // Generate dot globe quad data
      const dotGlobeData = generateDotQuadData(1, 0.012, 320, 500)
      _dotGlobeBuffer = createBuffer(dotGlobeData.vertexData, GPUBufferUsage.VERTEX)
      _dotGlobeIndexBuffer = createBuffer(dotGlobeData.indexData, GPUBufferUsage.INDEX)
      _dotGlobeIndexCount = dotGlobeData.indexCount
      console.log('[WebGPU] Dot globe quads:', _dotGlobeIndexCount / 6, 'dots,', _dotGlobeIndexCount, 'indices')
      
      // Generate glow marker geometry
      const glowMarkerData = generateGlowMarkerData(glowMarkers, 1.03, 0.035)
      _glowMarkerBuffer = createBuffer(glowMarkerData.vertexData, GPUBufferUsage.VERTEX)
      _glowMarkerIndexBuffer = createBuffer(glowMarkerData.indexData, GPUBufferUsage.INDEX)
      _glowMarkerIndexCount = glowMarkerData.indexCount
      _glowMarkerMeta = glowMarkerData.markerMeta
      console.log('[WebGPU] Glow markers:', glowMarkers.length, 'items,', _glowMarkerIndexCount, 'indices')
      
      const continentData = generateContinentGlobeData(1)
      _continentBuffer = createBuffer(continentData.vertexData, GPUBufferUsage.VERTEX)
      _continentIndexBuffer = createBuffer(continentData.indexData, GPUBufferUsage.INDEX)
      _continentIndexCount = continentData.count
      console.log('[WebGPU] Continent index count:', _continentIndexCount)
      
      const continentLineData = generateBoundaryLinesData(1)
      _continentLineBuffer = createBuffer(continentLineData.vertexData, GPUBufferUsage.VERTEX)
      _continentLineCount = continentLineData.count
      console.log('[WebGPU] Continent line count:', _continentLineCount)
      
      const countryData = generateCountryGlobeData(1)
      _countryBuffer = createBuffer(countryData.vertexData, GPUBufferUsage.VERTEX)
      _countryIndexBuffer = createBuffer(countryData.indexData, GPUBufferUsage.INDEX)
      _countryIndexCount = countryData.count
      console.log('[WebGPU] Country index count:', _countryIndexCount)
      
      const countryLineData = generateBoundaryLinesData(1)
      _countryLineBuffer = createBuffer(countryLineData.vertexData, GPUBufferUsage.VERTEX)
      _countryLineCount = countryLineData.count
      console.log('[WebGPU] Country line count:', _countryLineCount)
      
      const atmData = createSphereBuffers(1, 48, 24)
      _atmosphereVertexBuffer = atmData.vertexBuffer
      _atmosphereIndexBuffer = atmData.indexBuffer
      _atmosphereIndexCount = atmData.indexCount
      
      const starsData = createStars(3000)
      _starsVertexBuffer = starsData.buffer
      _starsCount = starsData.count
      
      const uniformBufferSize = 256
      _regionUniformBuffer = _device.createBuffer({
        size: uniformBufferSize,
        usage: GPUBufferUsage.UNIFORM | GPUBufferUsage.COPY_DST
      })
      _lineUniformBuffer = _device.createBuffer({
        size: uniformBufferSize,
        usage: GPUBufferUsage.UNIFORM | GPUBufferUsage.COPY_DST
      })
      _dotGlobeUniformBuffer = _device.createBuffer({
        size: uniformBufferSize,
        usage: GPUBufferUsage.UNIFORM | GPUBufferUsage.COPY_DST
      })
      _glowMarkerUniformBuffer = _device.createBuffer({
        size: uniformBufferSize,
        usage: GPUBufferUsage.UNIFORM | GPUBufferUsage.COPY_DST
      })
      _atmosphereUniformBuffer = _device.createBuffer({
        size: uniformBufferSize,
        usage: GPUBufferUsage.UNIFORM | GPUBufferUsage.COPY_DST
      })
      _starsUniformBuffer = _device.createBuffer({
        size: uniformBufferSize,
        usage: GPUBufferUsage.UNIFORM | GPUBufferUsage.COPY_DST
      })
      
      const regionShaderModule = _device.createShaderModule({ code: regionVertexShader })
      const lineShaderModule = _device.createShaderModule({ code: lineVertexShader })
      const dotGlobeShaderModule = _device.createShaderModule({ code: circleDotVertexShader })
      const glowMarkerShaderModule = _device.createShaderModule({ code: glowMarkerVertexShader })
      const atmosphereShaderModule = _device.createShaderModule({ code: atmosphereVertexShader })
      const starsShaderModule = _device.createShaderModule({ code: starsVertexShader })
      
      const uniformBindGroupLayout = _device.createBindGroupLayout({
        entries: [
          {
            binding: 0,
            visibility: GPUShaderStage.VERTEX | GPUShaderStage.FRAGMENT,
            buffer: { type: 'uniform' }
          }
        ]
      })
      
      const simpleLayout = _device.createPipelineLayout({
        bindGroupLayouts: [uniformBindGroupLayout]
      })
      
      _regionPipeline = _device.createRenderPipeline({
        layout: simpleLayout,
        vertex: {
          module: regionShaderModule,
          entryPoint: 'vs_main',
          buffers: [
            {
              arrayStride: 24,
              attributes: [
                { shaderLocation: 0, offset: 0, format: 'float32x3' },
                { shaderLocation: 1, offset: 12, format: 'float32x3' }
              ]
            }
          ]
        },
        fragment: {
          module: regionShaderModule,
          entryPoint: 'fs_main',
          targets: [{ format: _format }]
        },
        primitive: {
          topology: 'triangle-list',
          cullMode: 'back'
        },
        depthStencil: {
          depthWriteEnabled: true,
          depthCompare: 'less',
          format: 'depth24plus'
        }
      })
      
      _linePipeline = _device.createRenderPipeline({
        layout: simpleLayout,
        vertex: {
          module: lineShaderModule,
          entryPoint: 'vs_main',
          buffers: [
            {
              arrayStride: 24,
              attributes: [
                { shaderLocation: 0, offset: 0, format: 'float32x3' },
                { shaderLocation: 1, offset: 12, format: 'float32x3' }
              ]
            }
          ]
        },
        fragment: {
          module: lineShaderModule,
          entryPoint: 'fs_main',
          targets: [{ format: _format }]
        },
        primitive: { topology: 'line-list' },
        depthStencil: {
          depthWriteEnabled: true,
          depthCompare: 'less',
          format: 'depth24plus'
        }
      })
      
      _dotGlobePipeline = _device.createRenderPipeline({
        layout: simpleLayout,
        vertex: {
          module: dotGlobeShaderModule,
          entryPoint: 'vs_main',
          buffers: [
            {
              arrayStride: 32,
              attributes: [
                { shaderLocation: 0, offset: 0, format: 'float32x3' },
                { shaderLocation: 1, offset: 12, format: 'float32x2' },
                { shaderLocation: 2, offset: 20, format: 'float32x3' }
              ]
            }
          ]
        },
        fragment: {
          module: dotGlobeShaderModule,
          entryPoint: 'fs_main',
          targets: [{
            format: _format,
            blend: {
              color: { srcFactor: 'src-alpha', dstFactor: 'one-minus-src-alpha', operation: 'add' },
              alpha: { srcFactor: 'one', dstFactor: 'one-minus-src-alpha', operation: 'add' }
            }
          }]
        },
        primitive: {
          topology: 'triangle-list',
          cullMode: 'back'
        },
        depthStencil: {
          depthWriteEnabled: true,
          depthCompare: 'less',
          format: 'depth24plus'
        }
      })
      
      _glowMarkerPipeline = _device.createRenderPipeline({
        layout: simpleLayout,
        vertex: {
          module: glowMarkerShaderModule,
          entryPoint: 'vs_main',
          buffers: [
            {
              arrayStride: 24,
              attributes: [
                { shaderLocation: 0, offset: 0, format: 'float32x3' },
                { shaderLocation: 1, offset: 12, format: 'float32x3' }
              ]
            }
          ]
        },
        fragment: {
          module: glowMarkerShaderModule,
          entryPoint: 'fs_main',
          targets: [{
            format: _format,
            blend: {
              color: { srcFactor: 'src-alpha', dstFactor: 'one', operation: 'add' },
              alpha: { srcFactor: 'one-minus-src-alpha', dstFactor: 'one', operation: 'add' }
            }
          }]
        },
        primitive: {
          topology: 'triangle-list',
          cullMode: 'none'
        },
        depthStencil: {
          depthWriteEnabled: false,
          depthCompare: 'less',
          format: 'depth24plus'
        }
      })
      
      _atmospherePipeline = _device.createRenderPipeline({
        layout: simpleLayout,
        vertex: {
          module: atmosphereShaderModule,
          entryPoint: 'vs_main',
          buffers: [
            {
              arrayStride: 24,
              attributes: [
                { shaderLocation: 0, offset: 0, format: 'float32x3' },
                { shaderLocation: 1, offset: 12, format: 'float32x3' }
              ]
            }
          ]
        },
        fragment: {
          module: atmosphereShaderModule,
          entryPoint: 'fs_main',
          targets: [
            {
              format: _format,
              blend: {
                color: { srcFactor: 'src-alpha', dstFactor: 'one', operation: 'add' },
                alpha: { srcFactor: 'src-alpha', dstFactor: 'one', operation: 'add' }
              }
            }
          ]
        },
        primitive: {
          topology: 'triangle-list',
          cullMode: 'front'
        },
        depthStencil: {
          depthWriteEnabled: false,
          depthCompare: 'less',
          format: 'depth24plus'
        }
      })
      
      _starsPipeline = _device.createRenderPipeline({
        layout: simpleLayout,
        vertex: {
          module: starsShaderModule,
          entryPoint: 'vs_main',
          buffers: [
            {
              arrayStride: 24,
              attributes: [
                { shaderLocation: 0, offset: 0, format: 'float32x3' },
                { shaderLocation: 1, offset: 12, format: 'float32x3' }
              ]
            }
          ]
        },
        fragment: {
          module: starsShaderModule,
          entryPoint: 'fs_main',
          targets: [{ format: _format }]
        },
        primitive: { topology: 'point-list' },
        depthStencil: {
          depthWriteEnabled: false,
          depthCompare: 'less',
          format: 'depth24plus'
        }
      })
      
      _regionBindGroup = _device.createBindGroup({
        layout: uniformBindGroupLayout,
        entries: [{ binding: 0, resource: { buffer: _regionUniformBuffer } }]
      })
      _lineBindGroup = _device.createBindGroup({
        layout: uniformBindGroupLayout,
        entries: [{ binding: 0, resource: { buffer: _lineUniformBuffer } }]
      })
      _dotGlobeBindGroup = _device.createBindGroup({
        layout: uniformBindGroupLayout,
        entries: [{ binding: 0, resource: { buffer: _dotGlobeUniformBuffer } }]
      })
      _glowMarkerBindGroup = _device.createBindGroup({
        layout: uniformBindGroupLayout,
        entries: [{ binding: 0, resource: { buffer: _glowMarkerUniformBuffer } }]
      })
      _atmosphereBindGroup = _device.createBindGroup({
        layout: uniformBindGroupLayout,
        entries: [{ binding: 0, resource: { buffer: _atmosphereUniformBuffer } }]
      })
      _starsBindGroup = _device.createBindGroup({
        layout: uniformBindGroupLayout,
        entries: [{ binding: 0, resource: { buffer: _starsUniformBuffer } }]
      })
      
      isReady.value = true
      console.log('[WebGPU] Region globe pipeline ready.')
      return true
    } catch (e) {
      errorMessage.value = `渲染管线创建失败: ${e.message}`
      console.error('[WebGPU] Pipeline creation error:', e)
      return false
    }
  }

  function ensureDepthTexture(width, height) {
    if (!_depthTexture || _depthTexture.width !== width || _depthTexture.height !== height) {
      if (_depthTexture) {
        _depthTexture.destroy()
      }
      _depthTexture = _device.createTexture({
        size: [width, height],
        format: 'depth24plus',
        usage: GPUTextureUsage.RENDER_ATTACHMENT
      })
      _depthTextureView = _depthTexture.createView()
    }
  }

  function updateUniforms(buffer, data) {
    const arrayBuffer = new ArrayBuffer(256)
    const floatView = new Float32Array(arrayBuffer)
    if (data.mvpMatrix) {
      floatView.set(data.mvpMatrix, 0)
    }
    if (data.normalMatrix) {
      floatView.set(data.normalMatrix, 16)
    }
    if (data.lightDirection) {
      floatView.set(data.lightDirection, 32)
    }
    if (data.cameraPosition) {
      floatView.set(data.cameraPosition, 36)
    }
    if (data.time !== undefined) {
      floatView[40] = data.time
    }
    if (data.atmosphereStrength !== undefined) {
      floatView[41] = data.atmosphereStrength
    }
    _device.queue.writeBuffer(buffer, 0, arrayBuffer, 0, arrayBuffer.byteLength)
  }

  function renderFrame(renderData) {
    if (!_device || !_context || !isReady.value) return
    const canvas = canvasRef.value
    if (!canvas) return
    const width = canvas.clientWidth * window.devicePixelRatio
    const height = canvas.clientHeight * window.devicePixelRatio
    ensureDepthTexture(width, height)
    const commandEncoder = _device.createCommandEncoder()
    const textureView = _context.getCurrentTexture().createView()
    const renderPass = commandEncoder.beginRenderPass({
      colorAttachments: [
        {
          view: textureView,
          clearValue: renderData.clearColor || { r: 0, g: 0.01, b: 0.03, a: 1 },
          loadOp: 'clear',
          storeOp: 'store'
        }
      ],
      depthStencilAttachment: {
        view: _depthTextureView,
        depthClearValue: 1,
        depthLoadOp: 'clear',
        depthStoreOp: 'store'
      }
    })
    
    updateUniforms(_starsUniformBuffer, {
      mvpMatrix: renderData.starsVPMatrix,
      time: renderData.time
    })
    renderPass.setPipeline(_starsPipeline)
    renderPass.setBindGroup(0, _starsBindGroup)
    renderPass.setVertexBuffer(0, _starsVertexBuffer)
    renderPass.draw(_starsCount)
    
    updateUniforms(_atmosphereUniformBuffer, {
      mvpMatrix: renderData.mvpMatrix,
      cameraPosition: renderData.cameraPosition,
      time: renderData.time
    })
    renderPass.setPipeline(_atmospherePipeline)
    renderPass.setBindGroup(0, _atmosphereBindGroup)
    renderPass.setVertexBuffer(0, _atmosphereVertexBuffer)
    renderPass.setIndexBuffer(_atmosphereIndexBuffer, 'uint32')
    renderPass.drawIndexed(_atmosphereIndexCount)
    
    // Render dot globe as base land layer
    updateUniforms(_dotGlobeUniformBuffer, {
      mvpMatrix: renderData.mvpMatrix,
      normalMatrix: renderData.normalMatrix,
      lightDirection: renderData.lightDirection,
      cameraPosition: renderData.cameraPosition,
      time: renderData.time,
      atmosphereStrength: renderData.atmosphereStrength || 0.5
    })
    renderPass.setPipeline(_dotGlobePipeline)
    renderPass.setBindGroup(0, _dotGlobeBindGroup)
    renderPass.setVertexBuffer(0, _dotGlobeBuffer)
    renderPass.setIndexBuffer(_dotGlobeIndexBuffer, 'uint32')
    renderPass.drawIndexed(_dotGlobeIndexCount)
    
    // Render glow markers on top
    if (_glowMarkerIndexCount > 0) {
      updateUniforms(_glowMarkerUniformBuffer, {
        mvpMatrix: renderData.mvpMatrix,
        normalMatrix: renderData.normalMatrix,
        lightDirection: renderData.lightDirection,
        cameraPosition: renderData.cameraPosition,
        time: renderData.time,
        atmosphereStrength: renderData.atmosphereStrength || 0.5
      })
      renderPass.setPipeline(_glowMarkerPipeline)
      renderPass.setBindGroup(0, _glowMarkerBindGroup)
      renderPass.setVertexBuffer(0, _glowMarkerBuffer)
      renderPass.setIndexBuffer(_glowMarkerIndexBuffer, 'uint32')
      renderPass.drawIndexed(_glowMarkerIndexCount)
    }
    
    const showCountries = renderData.showCountries !== undefined ? renderData.showCountries : (_currentLevel.value === 'country')
    
    if (showCountries) {
      // updateUniforms(_regionUniformBuffer, {
      //   mvpMatrix: renderData.mvpMatrix,
      //   normalMatrix: renderData.normalMatrix,
      //   lightDirection: renderData.lightDirection,
      //   cameraPosition: renderData.cameraPosition,
      //   time: renderData.time,
      //   atmosphereStrength: renderData.atmosphereStrength || 0.5
      // })
      // renderPass.setPipeline(_regionPipeline)
      // renderPass.setBindGroup(0, _regionBindGroup)
      // renderPass.setVertexBuffer(0, _countryBuffer)
      // renderPass.setIndexBuffer(_countryIndexBuffer, 'uint32')
      // renderPass.drawIndexed(_countryIndexCount)
      
      updateUniforms(_lineUniformBuffer, {
        mvpMatrix: renderData.mvpMatrix,
        time: renderData.time
      })
      renderPass.setPipeline(_linePipeline)
      renderPass.setBindGroup(0, _lineBindGroup)
      renderPass.setVertexBuffer(0, _countryLineBuffer)
      renderPass.draw(_countryLineCount)
    }
    
    renderPass.end()
    _device.queue.submit([commandEncoder.finish()])
  }

  function setRegionLevel(level) {
    _currentLevel.value = level
  }

  function resize() {
    const canvas = canvasRef.value
    if (!canvas || !_device) return
    const width = canvas.clientWidth * window.devicePixelRatio
    const height = canvas.clientHeight * window.devicePixelRatio
    canvas.width = width
    canvas.height = height
  }

  function destroy() {
    if (_regionUniformBuffer) _regionUniformBuffer.destroy()
    if (_lineUniformBuffer) _lineUniformBuffer.destroy()
    if (_dotGlobeUniformBuffer) _dotGlobeUniformBuffer.destroy()
    if (_atmosphereUniformBuffer) _atmosphereUniformBuffer.destroy()
    if (_starsUniformBuffer) _starsUniformBuffer.destroy()
    if (_continentBuffer) _continentBuffer.destroy()
    if (_continentIndexBuffer) _continentIndexBuffer.destroy()
    if (_continentLineBuffer) _continentLineBuffer.destroy()
    if (_countryBuffer) _countryBuffer.destroy()
    if (_countryIndexBuffer) _countryIndexBuffer.destroy()
    if (_countryLineBuffer) _countryLineBuffer.destroy()
    if (_dotGlobeBuffer) _dotGlobeBuffer.destroy()
    if (_dotGlobeIndexBuffer) _dotGlobeIndexBuffer.destroy()
    if (_glowMarkerUniformBuffer) _glowMarkerUniformBuffer.destroy()
    if (_glowMarkerBuffer) _glowMarkerBuffer.destroy()
    if (_glowMarkerIndexBuffer) _glowMarkerIndexBuffer.destroy()
    if (_atmosphereVertexBuffer) _atmosphereVertexBuffer.destroy()
    if (_atmosphereIndexBuffer) _atmosphereIndexBuffer.destroy()
    if (_starsVertexBuffer) _starsVertexBuffer.destroy()
    if (_depthTexture) _depthTexture.destroy()
    _device = null
    _context = null
    _regionPipeline = null
    _linePipeline = null
    _atmospherePipeline = null
    _starsPipeline = null
  }

  onUnmounted(() => {
    destroy()
  })

  return {
    device,
    context,
    format,
    isSupported,
    isReady,
    errorMessage,
    currentLevel: _currentLevel,
    initWebGPU,
    initRenderPipeline,
    renderFrame,
    setRegionLevel,
    resize,
    destroy,
    getGlowMarkerMeta: () => _glowMarkerMeta
  }
}
