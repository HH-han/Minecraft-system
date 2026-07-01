export const safeParse = (jsonString, defaultValue = null) => {
  try {
    return JSON.parse(jsonString)
  } catch {
    return defaultValue
  }
}

export const safeStringify = (value, replacer = null, space = 2) => {
  try {
    return JSON.stringify(value, replacer, space)
  } catch {
    return ''
  }
}

export const cloneDeep = (obj) => {
  if (obj === null || typeof obj !== 'object') return obj
  if (Array.isArray(obj)) return obj.map(item => cloneDeep(item))
  const cloned = {}
  for (const key of Object.keys(obj)) {
    cloned[key] = cloneDeep(obj[key])
  }
  return cloned
}

export const isJsonString = (str) => {
  if (typeof str !== 'string') return false
  try {
    JSON.parse(str)
    return true
  } catch {
    return false
  }
}