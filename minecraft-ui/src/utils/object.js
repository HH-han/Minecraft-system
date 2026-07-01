export const pick = (obj, keys) => {
  const result = {}
  for (const key of keys) {
    if (key in obj) result[key] = obj[key]
  }
  return result
}

export const omit = (obj, keys) => {
  const result = { ...obj }
  for (const key of keys) {
    delete result[key]
  }
  return result
}

export const isEmpty = (obj) => {
  if (obj === null || obj === undefined) return true
  if (typeof obj !== 'object') return true
  return Object.keys(obj).length === 0
}

export const merge = (target, ...sources) => {
  const result = { ...target }
  for (const source of sources) {
    for (const [key, value] of Object.entries(source)) {
      if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
        result[key] = merge(result[key] || {}, value)
      } else {
        result[key] = value
      }
    }
  }
  return result
}

export const get = (obj, path, defaultValue = undefined) => {
  const keys = path.split('.')
  let result = obj
  for (const key of keys) {
    if (result === null || result === undefined) return defaultValue
    result = result[key]
  }
  return result !== undefined ? result : defaultValue
}

export const set = (obj, path, value) => {
  const keys = path.split('.')
  const lastKey = keys.pop()
  let result = obj
  for (const key of keys) {
    if (!result[key]) result[key] = {}
    result = result[key]
  }
  result[lastKey] = value
  return obj
}