import { ref, onUnmounted, shallowRef } from 'vue'
import { generateContinentGlobeData, generateCountryGlobeData, generateBoundaryLinesData } from '../utils/regionData.js'

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

const continentShapes = [
  { id: 'asia', centerLat: 40, centerLng: 95, latExtent: 35, lngExtent: 60, irregularity: 0.35 },
  { id: 'europe', centerLat: 52, centerLng: 15, latExtent: 20, lngExtent: 25, irregularity: 0.4 },
  { id: 'africa', centerLat: 5, centerLng: 20, latExtent: 35, lngExtent: 22, irregularity: 0.3 },
  { id: 'north-america', centerLat: 45, centerLng: -100, latExtent: 30, lngExtent: 45, irregularity: 0.35 },
  { id: 'south-america', centerLat: -15, centerLng: -60, latExtent: 30, lngExtent: 18, irregularity: 0.35 },
  { id: 'oceania', centerLat: -22, centerLng: 135, latExtent: 18, lngExtent: 22, irregularity: 0.4 },
  { id: 'antarctica', centerLat: -82, centerLng: 0, latExtent: 12, lngExtent: 180, irregularity: 0.2 }
]

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
  let _regionUniformBuffer = null
  let _lineUniformBuffer = null
  let _atmosphereUniformBuffer = null
  let _starsUniformBuffer = null
  let _regionBindGroup = null
  let _lineBindGroup = null
  let _atmosphereBindGroup = null
  let _starsBindGroup = null
  
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
      console.log('[WebGPU] Creating region globe geometry...')
      
      const continentData = generateContinentGlobeData(1, continentShapes)
      _continentBuffer = createBuffer(continentData.vertexData, GPUBufferUsage.VERTEX)
      _continentIndexBuffer = createBuffer(continentData.indexData, GPUBufferUsage.INDEX)
      _continentIndexCount = continentData.count
      console.log('[WebGPU] Continent index count:', _continentIndexCount)
      
      const continentLineData = generateBoundaryLinesData(1, continentShapes)
      _continentLineBuffer = createBuffer(continentLineData.vertexData, GPUBufferUsage.VERTEX)
      _continentLineCount = continentLineData.count
      console.log('[WebGPU] Continent line count:', _continentLineCount)
      
      const countryData = generateCountryGlobeData(1)
      _countryBuffer = createBuffer(countryData.vertexData, GPUBufferUsage.VERTEX)
      _countryIndexBuffer = createBuffer(countryData.indexData, GPUBufferUsage.INDEX)
      _countryIndexCount = countryData.count
      console.log('[WebGPU] Country index count:', _countryIndexCount)
      
      const countries = [
        { continent: 'asia', centerLat: 35, lng: 105, latExtent: 15, lngExtent: 18, irregularity: 0.4 },
        { continent: 'north-america', centerLat: 38, lng: -97, latExtent: 12, lngExtent: 25, irregularity: 0.35 },
        { continent: 'europe', centerLat: 55, lng: 80, latExtent: 20, lngExtent: 60, irregularity: 0.4 },
        { continent: 'south-america', centerLat: -10, lng: -55, latExtent: 20, lngExtent: 18, irregularity: 0.35 },
        { continent: 'oceania', centerLat: -25, lng: 135, latExtent: 12, lngExtent: 18, irregularity: 0.4 },
        { continent: 'asia', centerLat: 20, lng: 78, latExtent: 12, lngExtent: 12, irregularity: 0.3 },
        { continent: 'asia', centerLat: 36, lng: 138, latExtent: 6, lngExtent: 6, irregularity: 0.35 },
        { continent: 'europe', centerLat: 51, lng: 10, latExtent: 4, lngExtent: 6, irregularity: 0.3 },
        { continent: 'europe', centerLat: 46, lng: 2, latExtent: 6, lngExtent: 6, irregularity: 0.3 },
        { continent: 'africa', centerLat: -28, lng: 25, latExtent: 8, lngExtent: 8, irregularity: 0.35 },
        { continent: 'africa', centerLat: 26, lng: 30, latExtent: 6, lngExtent: 6, irregularity: 0.3 },
        { continent: 'north-america', centerLat: 24, lng: -102, latExtent: 10, lngExtent: 15, irregularity: 0.35 },
        { continent: 'north-america', centerLat: 56, lng: -106, latExtent: 15, lngExtent: 30, irregularity: 0.4 },
        { continent: 'south-america', centerLat: -34, lng: -65, latExtent: 12, lngExtent: 12, irregularity: 0.35 },
        { continent: 'asia', centerLat: 48, lng: 67, latExtent: 10, lngExtent: 25, irregularity: 0.35 },
        { continent: 'asia', centerLat: 24, lng: 45, latExtent: 8, lngExtent: 15, irregularity: 0.3 },
        { continent: 'asia', centerLat: -5, lng: 120, latExtent: 12, lngExtent: 20, irregularity: 0.4 },
        { continent: 'asia', centerLat: 36, lng: 128, latExtent: 5, lngExtent: 5, irregularity: 0.3 },
        { continent: 'asia', centerLat: 39, lng: 35, latExtent: 8, lngExtent: 15, irregularity: 0.35 }
      ]
      const countryLineData = generateBoundaryLinesData(1, countries)
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
    
    const showCountries = renderData.showCountries !== undefined ? renderData.showCountries : (_currentLevel.value === 'country')
    
    if (showCountries) {
      updateUniforms(_regionUniformBuffer, {
        mvpMatrix: renderData.mvpMatrix,
        normalMatrix: renderData.normalMatrix,
        lightDirection: renderData.lightDirection,
        cameraPosition: renderData.cameraPosition,
        time: renderData.time,
        atmosphereStrength: renderData.atmosphereStrength || 0.5
      })
      renderPass.setPipeline(_regionPipeline)
      renderPass.setBindGroup(0, _regionBindGroup)
      renderPass.setVertexBuffer(0, _countryBuffer)
      renderPass.setIndexBuffer(_countryIndexBuffer, 'uint32')
      renderPass.drawIndexed(_countryIndexCount)
      
      updateUniforms(_lineUniformBuffer, {
        mvpMatrix: renderData.mvpMatrix,
        time: renderData.time
      })
      renderPass.setPipeline(_linePipeline)
      renderPass.setBindGroup(0, _lineBindGroup)
      renderPass.setVertexBuffer(0, _countryLineBuffer)
      renderPass.draw(_countryLineCount)
    } else {
      updateUniforms(_regionUniformBuffer, {
        mvpMatrix: renderData.mvpMatrix,
        normalMatrix: renderData.normalMatrix,
        lightDirection: renderData.lightDirection,
        cameraPosition: renderData.cameraPosition,
        time: renderData.time,
        atmosphereStrength: renderData.atmosphereStrength || 0.5
      })
      renderPass.setPipeline(_regionPipeline)
      renderPass.setBindGroup(0, _regionBindGroup)
      renderPass.setVertexBuffer(0, _continentBuffer)
      renderPass.setIndexBuffer(_continentIndexBuffer, 'uint32')
      renderPass.drawIndexed(_continentIndexCount)
      
      updateUniforms(_lineUniformBuffer, {
        mvpMatrix: renderData.mvpMatrix,
        time: renderData.time
      })
      renderPass.setPipeline(_linePipeline)
      renderPass.setBindGroup(0, _lineBindGroup)
      renderPass.setVertexBuffer(0, _continentLineBuffer)
      renderPass.draw(_continentLineCount)
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
    if (_atmosphereUniformBuffer) _atmosphereUniformBuffer.destroy()
    if (_starsUniformBuffer) _starsUniformBuffer.destroy()
    if (_continentBuffer) _continentBuffer.destroy()
    if (_continentIndexBuffer) _continentIndexBuffer.destroy()
    if (_continentLineBuffer) _continentLineBuffer.destroy()
    if (_countryBuffer) _countryBuffer.destroy()
    if (_countryIndexBuffer) _countryIndexBuffer.destroy()
    if (_countryLineBuffer) _countryLineBuffer.destroy()
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
    destroy
  }
}
