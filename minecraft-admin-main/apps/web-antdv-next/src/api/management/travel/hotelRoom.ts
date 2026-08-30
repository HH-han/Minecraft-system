// @ts-nocheck
import request from '#/utils/request';

export const addHotelRoom = (room: any): Promise<any> => {
  return request.post('/hotelRoom/add', room)
}

export const updateHotelRoom = (room: any): Promise<any> => {
  return request.put('/hotelRoom/update', room)
}

export const deleteHotelRoom = (id: any): Promise<any> => {
  return request.delete(`/hotelRoom/${id}`)
}

export const getHotelRooms = (hotelId: any): Promise<any> => {
  return request.get('/hotelRoom/list', { params: { hotelId } })
}