import request from '@/utils/request'

export const addHotelFacility = (facility) => {
  return request.post('/hotelFacility/add', facility)
}

export const deleteHotelFacility = (id) => {
  return request.delete(`/hotelFacility/${id}`)
}

export const getHotelFacilities = (hotelId) => {
  return request.get('/hotelFacility/list', { params: { hotelId } })
}