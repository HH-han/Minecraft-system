export const flatten = (arr) => {
  return arr.reduce((acc, val) => {
    return acc.concat(Array.isArray(val) ? flatten(val) : val)
  }, [])
}

export const unique = (arr) => {
  return [...new Set(arr)]
}

export const uniqueBy = (arr, key) => {
  const seen = new Set()
  return arr.filter(item => {
    const value = typeof key === 'function' ? key(item) : item[key]
    if (seen.has(value)) return false
    seen.add(value)
    return true
  })
}

export const chunk = (arr, size) => {
  const chunks = []
  for (let i = 0; i < arr.length; i += size) {
    chunks.push(arr.slice(i, i + size))
  }
  return chunks
}

export const shuffle = (arr) => {
  const result = [...arr]
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[result[i], result[j]] = [result[j], result[i]]
  }
  return result
}

export const groupBy = (arr, key) => {
  return arr.reduce((acc, item) => {
    const value = typeof key === 'function' ? key(item) : item[key]
    if (!acc[value]) acc[value] = []
    acc[value].push(item)
    return acc
  }, {})
}

export const sumBy = (arr, key) => {
  return arr.reduce((acc, item) => {
    const value = typeof key === 'function' ? key(item) : item[key]
    return acc + (Number(value) || 0)
  }, 0)
}

export const maxBy = (arr, key) => {
  if (arr.length === 0) return undefined
  return arr.reduce((max, item) => {
    const value = typeof key === 'function' ? key(item) : item[key]
    return value > max[key] ? item : max
  })
}

export const minBy = (arr, key) => {
  if (arr.length === 0) return undefined
  return arr.reduce((min, item) => {
    const value = typeof key === 'function' ? key(item) : item[key]
    return value < min[key] ? item : min
  })
}

export const sortBy = (arr, key, order = 'asc') => {
  return [...arr].sort((a, b) => {
    const valA = typeof key === 'function' ? key(a) : a[key]
    const valB = typeof key === 'function' ? key(b) : b[key]
    if (valA < valB) return order === 'asc' ? -1 : 1
    if (valA > valB) return order === 'asc' ? 1 : -1
    return 0
  })
}