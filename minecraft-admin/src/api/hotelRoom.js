import request from '@/utils/request'

export const addHotelRoom = (room) => {
  return request.post('/hotelRoom/add', room)
}

export const updateHotelRoom = (room) => {
  return request.put('/hotelRoom/update', room)
}

export const deleteHotelRoom = (id) => {
  return request.delete(`/hotelRoom/${id}`)
}

export const getHotelRooms = (hotelId) => {
  return request.get('/hotelRoom/list', { params: { hotelId } })
}