export const downloadFile = (url, filename) => {
  const link = document.createElement('a')
  link.href = url
  link.download = filename || url.split('/').pop()
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

export const downloadBlob = (blob, filename) => {
  const url = URL.createObjectURL(blob)
  downloadFile(url, filename)
  URL.revokeObjectURL(url)
}

export const downloadJson = (data, filename) => {
  const jsonString = JSON.stringify(data, null, 2)
  const blob = new Blob([jsonString], { type: 'application/json' })
  downloadBlob(blob, filename || 'data.json')
}

export const downloadText = (text, filename) => {
  const blob = new Blob([text], { type: 'text/plain' })
  downloadBlob(blob, filename || 'text.txt')
}