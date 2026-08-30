// @ts-nocheck
import request from '#/utils/request';

export const addHotelFacility = (facility: any): Promise<any> => {
  return request.post('/hotelFacility/add', facility)
}

export const deleteHotelFacility = (id: any): Promise<any> => {
  return request.delete(`/hotelFacility/${id}`)
}

export const getHotelFacilities = (hotelId: any): Promise<any> => {
  return request.get('/hotelFacility/list', { params: { hotelId } })
}