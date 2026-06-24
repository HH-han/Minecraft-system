import { ref, onUnmounted, shallowRef } from 'vue'
import { generateDotGlobeData } from '../utils/dotGlobe.js'

const earthDotsVertexShader = /* wgsl */ `
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
  let normal = normalize(input.worldPos);
  let lightDir = normalize(uniforms.lightDirection);
  let lighting = max(dot(normal, lightDir), 0.3);
  let finalColor = input.color * lighting * 1.3;
  return vec4<f32>(finalColor, 1.0);
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
  let _dotsPipeline = null
  let _atmospherePipeline = null
  let _starsPipeline = null
  let _dotsUniformBuffer = null
  let _atmosphereUniformBuffer = null
  let _starsUniformBuffer = null
  let _dotsBindGroup = null
  let _atmosphereBindGroup = null
  let _starsBindGroup = null
  let _earthDotsBuffer = null
  let _earthDotsCount = 0
  let _atmosphereVertexBuffer = null
  let _atmosphereIndexBuffer = null
  let _atmosphereIndexCount = 0
  let _starsVertexBuffer = null
  let _starsCount = 0
  let _depthTexture = null
  let _depthTextureView = null

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
      console.log('[WebGPU] Creating dot globe geometry...')
      
      const dotsData = generateDotGlobeData(1, 120, 240)
      _earthDotsBuffer = createBuffer(dotsData.vertexData, GPUBufferUsage.VERTEX)
      _earthDotsCount = dotsData.count
      console.log('[WebGPU] Earth dots count:', _earthDotsCount)
      
      const atmData = createSphereBuffers(1, 48, 24)
      _atmosphereVertexBuffer = atmData.vertexBuffer
      _atmosphereIndexBuffer = atmData.indexBuffer
      _atmosphereIndexCount = atmData.indexCount
      
      const starsData = createStars(3000)
      _starsVertexBuffer = starsData.buffer
      _starsCount = starsData.count
      
      const uniformBufferSize = 256
      _dotsUniformBuffer = _device.createBuffer({
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
      
      const dotsShaderModule = _device.createShaderModule({ code: earthDotsVertexShader })
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
      
      _dotsPipeline = _device.createRenderPipeline({
        layout: simpleLayout,
        vertex: {
          module: dotsShaderModule,
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
          module: dotsShaderModule,
          entryPoint: 'fs_main',
          targets: [{ format: _format }]
        },
        primitive: { topology: 'point-list' },
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
      
      _dotsBindGroup = _device.createBindGroup({
        layout: uniformBindGroupLayout,
        entries: [{ binding: 0, resource: { buffer: _dotsUniformBuffer } }]
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
      console.log('[WebGPU] Dot globe pipeline ready. Dots count:', _earthDotsCount)
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
    
    updateUniforms(_dotsUniformBuffer, {
      mvpMatrix: renderData.mvpMatrix,
      normalMatrix: renderData.normalMatrix,
      lightDirection: renderData.lightDirection,
      cameraPosition: renderData.cameraPosition,
      time: renderData.time,
      atmosphereStrength: renderData.atmosphereStrength || 0.5
    })
    renderPass.setPipeline(_dotsPipeline)
    renderPass.setBindGroup(0, _dotsBindGroup)
    renderPass.setVertexBuffer(0, _earthDotsBuffer)
    renderPass.draw(_earthDotsCount)
    
    renderPass.end()
    _device.queue.submit([commandEncoder.finish()])
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
    if (_dotsUniformBuffer) _dotsUniformBuffer.destroy()
    if (_atmosphereUniformBuffer) _atmosphereUniformBuffer.destroy()
    if (_starsUniformBuffer) _starsUniformBuffer.destroy()
    if (_earthDotsBuffer) _earthDotsBuffer.destroy()
    if (_atmosphereVertexBuffer) _atmosphereVertexBuffer.destroy()
    if (_atmosphereIndexBuffer) _atmosphereIndexBuffer.destroy()
    if (_starsVertexBuffer) _starsVertexBuffer.destroy()
    if (_depthTexture) _depthTexture.destroy()
    _device = null
    _context = null
    _dotsPipeline = null
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
    initWebGPU,
    initRenderPipeline,
    renderFrame,
    resize,
    destroy
  }
}
