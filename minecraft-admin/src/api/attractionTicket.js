import request from '@/utils/request'

export const addAttractionTicket = (ticket) => {
  return request.post('/attractionTicket/add', ticket)
}

export const updateAttractionTicket = (ticket) => {
  return request.put('/attractionTicket/update', ticket)
}

export const deleteAttractionTicket = (id) => {
  return request.delete(`/attractionTicket/${id}`)
}

export const getAttractionTickets = (attractionId) => {
  return request.get('/attractionTicket/list', { params: { attractionId } })
}