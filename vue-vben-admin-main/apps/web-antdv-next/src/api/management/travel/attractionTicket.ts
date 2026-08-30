// @ts-nocheck
import request from '#/utils/request';

export const addAttractionTicket = (ticket: any): Promise<any> => {
  return request.post('/attractionTicket/add', ticket)
}

export const updateAttractionTicket = (ticket: any): Promise<any> => {
  return request.put('/attractionTicket/update', ticket)
}

export const deleteAttractionTicket = (id: any): Promise<any> => {
  return request.delete(`/attractionTicket/${id}`)
}

export const getAttractionTickets = (attractionId: any): Promise<any> => {
  return request.get('/attractionTicket/list', { params: { attractionId } })
}