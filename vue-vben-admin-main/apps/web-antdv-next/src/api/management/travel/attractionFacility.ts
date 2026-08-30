// @ts-nocheck
import request from '#/utils/request';

export const addAttractionFacility = (facility: any): Promise<any> => {
  return request.post('/attractionFacility/add', facility)
}

export const deleteAttractionFacility = (id: any): Promise<any> => {
  return request.delete(`/attractionFacility/${id}`)
}

export const getAttractionFacilities = (attractionId: any): Promise<any> => {
  return request.get('/attractionFacility/list', { params: { attractionId } })
}