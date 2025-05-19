import api from './index'

export const createWorkOrder = (vehicleId: number) => {
  return api.post(`/orders?vehicleId=${vehicleId}`)
}

export const updateOrderStatus = (id: number, status: string) => {
  return api.put(`/orders/${id}/status?status=${status}`)
}

export const addMaterial = (id: number, data: any) => {
  return api.post(`/orders/${id}/material`, data)
}

export const addWorkHour = (id: number, data: any) => {
  return api.post(`/orders/${id}/hour`, data)
}