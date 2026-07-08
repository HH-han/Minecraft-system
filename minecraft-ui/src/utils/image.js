/**
 * 图片加载工具函数
 * 使用 Promise 封装图片加载过程，支持超时控制和跨域设置
 * 
 * @param {string} src - 图片资源地址
 * @param {Object} [options] - 加载配置选项
 * @param {number} [options.timeout=5000] - 超时时间（毫秒），0表示不超时
 * @param {string} [options.crossorigin='anonymous'] - 跨域设置
 * @returns {Promise<HTMLImageElement>} 加载成功返回Image对象，失败返回错误
 */
export const loadImage = (src, options = {}) => {
  return new Promise((resolve, reject) => {
    const { timeout = 5000, crossorigin = 'anonymous' } = options
    
    const img = new Image()
    img.crossOrigin = crossorigin
    
    let timer = null
    if (timeout > 0) {
      timer = setTimeout(() => {
        img.onload = null
        img.onerror = null
        reject(new Error(`Image load timeout after ${timeout}ms`))
      }, timeout)
    }
    
    img.onload = () => {
      if (timer) clearTimeout(timer)
      resolve(img)
    }
    
    img.onerror = (e) => {
      if (timer) clearTimeout(timer)
      reject(e instanceof Error ? e : new Error('Image load failed'))
    }
    
    img.src = src
  })
}

/**
 * 生成验证码 SVG 图片
 * 随机生成带背景色和装饰元素的 SVG 验证码背景图
 * 
 * @param {number} [width=320] - 图片宽度
 * @param {number} [height=180] - 图片高度
 * @returns {string} SVG 字符串
 */
export const generateCaptchaSVG = (width = 320, height = 180) => {
  const colors = ['#b0c4de', '#f7dc6f', '#a3d8a3', '#f5b7b1', '#d7bde2', '#aed6f1']
  const bgColor = colors[Math.floor(Math.random() * colors.length)]
  const accentColor = colors[Math.floor(Math.random() * colors.length)]
  
  const cx = 80 + Math.floor(Math.random() * 160)
  const cy = 40 + Math.floor(Math.random() * 100)
  const r = 30 + Math.floor(Math.random() * 50)
  
  return `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}">
    <rect width="${width}" height="${height}" fill="${bgColor}"/>
    <circle cx="${cx}" cy="${cy}" r="${r}" fill="${accentColor}" opacity="0.6"/>
    <rect x="60" y="30" width="200" height="120" fill="none" stroke="#2c3e50" stroke-width="2" opacity="0.2"/>
    <circle cx="${160 + Math.floor(Math.random() * 40)}" cy="${80 + Math.floor(Math.random() * 40)}" r="${10 + Math.floor(Math.random() * 20)}" fill="#4a6cf7" opacity="0.15"/>
  </svg>`
}

/**
 * 将 SVG 字符串转换为 Base64 编码的 Data URL
 * 
 * @param {string} svgContent - SVG 字符串内容
 * @returns {string} Base64 编码的 Data URL，转换失败返回空字符串
 */
export const svgToBase64 = (svgContent) => {
  try {
    return 'data:image/svg+xml;base64,' + btoa(encodeURIComponent(svgContent).replace(/%([0-9A-F]{2})/g, (match, p1) => String.fromCharCode(parseInt(p1, 16))))
  } catch (e) {
    console.error('SVG to base64 conversion failed:', e)
    return ''
  }
}

/**
 * 绘制拼图形状路径
 * 使用二次贝塞尔曲线绘制带有缺口的拼图块形状
 * 
 * @param {CanvasRenderingContext2D} ctx - Canvas 2D 上下文
 * @param {number} x - 形状左上角 X 坐标
 * @param {number} y - 形状左上角 Y 坐标
 * @param {number} width - 形状宽度
 * @param {number} height - 形状高度
 * @param {boolean} [isMask=false] - 是否为遮罩模式（缺口方向相反）
 */
export const drawPuzzleShape = (ctx, x, y, width, height, isMask = false) => {
  const radius = 5
  const notchSize = 7
  
  ctx.beginPath()
  
  ctx.moveTo(x + radius, y)
  ctx.lineTo(x + width - radius, y)
  ctx.quadraticCurveTo(x + width, y, x + width, y + radius)
  ctx.lineTo(x + width, y + height / 2 - notchSize)
  
  if (isMask) {
    ctx.quadraticCurveTo(x + width + notchSize / 2, y + height / 2, x + width, y + height / 2 + notchSize)
  } else {
    ctx.quadraticCurveTo(x + width - notchSize / 2, y + height / 2, x + width, y + height / 2 + notchSize)
  }
  
  ctx.lineTo(x + width, y + height - radius)
  ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height)
  ctx.lineTo(x + radius, y + height)
  ctx.quadraticCurveTo(x, y + height, x, y + height - radius)
  ctx.lineTo(x, y + height / 2 + notchSize)
  
  if (isMask) {
    ctx.quadraticCurveTo(x - notchSize / 2, y + height / 2, x, y + height / 2 - notchSize)
  } else {
    ctx.quadraticCurveTo(x + notchSize / 2, y + height / 2, x, y + height / 2 - notchSize)
  }
  
  ctx.lineTo(x, y + radius)
  ctx.quadraticCurveTo(x, y, x + radius, y)
  ctx.closePath()
}

/**
 * 绘制主画布（背景遮罩 + 目标缺口）
 * 在主画布上绘制拼图块的遮罩区域和目标位置的缺口轮廓
 * 
 * @param {HTMLCanvasElement} canvas - 主画布元素
 * @param {number} puzzleOffset - 拼图块原始位置 X 偏移
 * @param {number} puzzleY - 拼图块 Y 坐标
 * @param {number} puzzleSize - 拼图块尺寸
 * @param {number} targetX - 目标位置 X 坐标
 * @param {Object} [captchaData] - 验证码数据（包含后端返回的 targetX）
 * @param {number} containerWidth - 容器宽度
 * @param {number} containerHeight - 容器高度
 */
export const drawMainCanvas = (canvas, puzzleOffset, puzzleY, puzzleSize, targetX, captchaData, containerWidth, containerHeight) => {
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  
  canvas.width = containerWidth
  canvas.height = containerHeight
  
  ctx.clearRect(0, 0, containerWidth, containerHeight)
  
  drawPuzzleShape(ctx, puzzleOffset, puzzleY, puzzleSize, puzzleSize, true)
  ctx.fillStyle = 'rgba(0, 0, 0, 0.5)'
  ctx.fill()
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.3)'
  ctx.lineWidth = 1
  ctx.stroke()
  
  let targetPosX = targetX
  if (captchaData && captchaData.targetX !== undefined) {
    const scale = containerWidth / 320
    targetPosX = captchaData.targetX * scale
  }
  
  drawPuzzleShape(ctx, targetPosX, puzzleY, puzzleSize, puzzleSize, false)
  ctx.fillStyle = 'rgba(255, 255, 255, 0.1)'
  ctx.fill()
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.3)'
  ctx.lineWidth = 1
  ctx.stroke()
}

/**
 * 绘制拼图块画布
 * 从背景图中精确裁剪出拼图块区域并绘制到拼图块画布上
 * 
 * @param {HTMLCanvasElement} canvas - 拼图块画布元素
 * @param {HTMLImageElement} backgroundImage - 背景图片
 * @param {number} puzzleOffset - 拼图块原始位置 X 偏移（已缩放）
 * @param {number} puzzleY - 拼图块 Y 坐标（已缩放）
 * @param {number} puzzleSize - 拼图块尺寸（已缩放）
 * @param {number} containerWidth - 容器宽度
 */
export const drawPieceCanvas = (canvas, backgroundImage, puzzleOffset, puzzleY, puzzleSize, containerWidth) => {
  if (!canvas || !backgroundImage) return
  
  const ctx = canvas.getContext('2d')
  const scale = containerWidth / 320
  
  canvas.width = puzzleSize
  canvas.height = puzzleSize
  
  ctx.clearRect(0, 0, puzzleSize, puzzleSize)
  
  drawPuzzleShape(ctx, 0, 0, puzzleSize, puzzleSize, false)
  ctx.save()
  ctx.clip()
  
  const sx = puzzleOffset / scale
  const sy = puzzleY / scale
  const sWidth = puzzleSize / scale
  const sHeight = puzzleSize / scale
  
  ctx.drawImage(backgroundImage, sx, sy, sWidth, sHeight, 0, 0, puzzleSize, puzzleSize)
  
  ctx.restore()
  
  drawPuzzleShape(ctx, 0, 0, puzzleSize, puzzleSize, false)
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.8)'
  ctx.lineWidth = 1
  ctx.stroke()
}

/**
 * 验证滑块位置是否正确
 * 判断滑块拖动的位置与目标位置的偏差是否在容差范围内
 * 
 * @param {number} sliderPosition - 当前滑块位置
 * @param {number} targetOffset - 目标偏移量
 * @param {number} [tolerance=8] - 容差范围（像素）
 * @returns {boolean} 验证通过返回 true，否则返回 false
 */
export const verifySliderPosition = (sliderPosition, targetOffset, tolerance = 8) => {
  const diff = Math.abs(sliderPosition - targetOffset)
  return diff <= tolerance
}

/**
 * 计算滑块最大可拖动位置
 * 即目标位置与拼图块初始位置的差值
 * 
 * @param {number} targetX - 目标位置 X 坐标
 * @param {number} puzzleOffset - 拼图块初始位置偏移
 * @returns {number} 滑块最大可拖动距离
 */
export const calculateMaxPosition = (targetX, puzzleOffset) => {
  return targetX - puzzleOffset
}

/**
 * 将滑块位置转换为原始坐标系中的位置
 * 将当前容器中的滑块位置按比例转换回 320px 宽度基准的原始位置
 * 
 * @param {number} sliderPosition - 当前滑块位置（已缩放）
 * @param {number} containerWidth - 当前容器宽度
 * @returns {number} 原始坐标系中的滑块位置
 */
export const calculateScaledPosition = (sliderPosition, containerWidth) => {
  const scale = containerWidth / 320
  return Math.round(sliderPosition / scale)
}

/**
 * 生成追踪 ID
 * 用于标识每次验证码请求的唯一标识
 * 
 * @returns {string} 唯一的追踪 ID
 */
export const generateTraceId = () => {
  return 'captcha_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

/**
 * 计算验证码参数（用于本地生成模式）
 * 根据容器尺寸计算拼图块的位置和大小参数
 * 
 * @param {number} containerWidth - 容器宽度
 * @param {number} containerHeight - 容器高度
 * @param {number} [puzzleSize=44] - 拼图块基础尺寸
 * @returns {Object} 包含 puzzleSize, puzzleOffset, puzzleY, maxPosition 的参数对象
 */
export const calculateCaptchaParams = (containerWidth, containerHeight, puzzleSize = 44) => {
  const scale = containerWidth / 320
  const scaledSize = puzzleSize * scale
  
  const minOffset = 30
  const maxOffset = containerWidth - scaledSize - 60
  const puzzleOffset = Math.floor(Math.random() * (maxOffset - minOffset + 1)) + minOffset
  
  const targetX = containerWidth - scaledSize - 20
  const maxPosition = targetX - puzzleOffset
  
  const minY = 10
  const maxY = containerHeight - scaledSize - 20
  const puzzleY = Math.floor(Math.random() * (maxY - minY + 1)) + minY
  
  return {
    puzzleSize: scaledSize,
    puzzleOffset,
    puzzleY,
    maxPosition
  }
}

/**
 * 计算验证码参数（基于后端数据）
 * 根据后端返回的验证码数据和容器尺寸计算实际显示参数
 * 
 * @param {Object} captchaData - 后端返回的验证码数据
 * @param {number} containerWidth - 容器宽度
 * @param {number} containerHeight - 容器高度
 * @returns {Object} 包含 puzzleSize, puzzleOffset, puzzleY, maxPosition 的参数对象
 */
export const calculateCaptchaParamsFromData = (captchaData, containerWidth, containerHeight) => {
  const scale = containerWidth / 320
  
  const puzzleSize = (captchaData.pieceWidth || 44) * scale
  const puzzleOffset = (captchaData.puzzleX || Math.floor(Math.random() * (320 - 44 - 60)) + 30) * scale
  const puzzleY = (captchaData.puzzleY || Math.floor(Math.random() * (180 - 44 - 20)) + 10) * scale
  
  const targetX = captchaData.targetX || (320 - 44 - 20)
  const targetXScaled = targetX * scale
  const maxPosition = targetXScaled - puzzleOffset
  
  return {
    puzzleSize,
    puzzleOffset,
    puzzleY,
    maxPosition
  }
}