export const trim = (str) => {
  return str ? str.trim() : ''
}

export const trimStart = (str) => {
  return str ? str.trimStart() : ''
}

export const trimEnd = (str) => {
  return str ? str.trimEnd() : ''
}

export const toLowerCase = (str) => {
  return str ? str.toLowerCase() : ''
}

export const toUpperCase = (str) => {
  return str ? str.toUpperCase() : ''
}

export const capitalize = (str) => {
  if (!str) return ''
  return str.charAt(0).toUpperCase() + str.slice(1)
}

export const camelCase = (str) => {
  if (!str) return ''
  return str.replace(/-([a-z])/g, (_, c) => c.toUpperCase())
}

export const kebabCase = (str) => {
  if (!str) return ''
  return str.replace(/([a-z0-9])([A-Z])/g, '$1-$2').toLowerCase()
}

export const truncate = (str, maxLength, suffix = '...') => {
  if (!str) return ''
  if (str.length <= maxLength) return str
  return str.slice(0, maxLength) + suffix
}

export const removeHtmlTags = (str) => {
  if (!str) return ''
  return str.replace(/<[^>]*>/g, '')
}

export const escapeHtml = (str) => {
  if (!str) return ''
  const div = document.createElement('div')
  div.textContent = str
  return div.innerHTML
}

export const unescapeHtml = (str) => {
  if (!str) return ''
  const div = document.createElement('div')
  div.innerHTML = str
  return div.textContent
}