import request from '@/utils/request'

export const addAttractionFacility = (facility) => {
  return request.post('/attractionFacility/add', facility)
}

export const deleteAttractionFacility = (id) => {
  return request.delete(`/attractionFacility/${id}`)
}

export const getAttractionFacilities = (attractionId) => {
  return request.get('/attractionFacility/list', { params: { attractionId } })
}