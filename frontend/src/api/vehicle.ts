import api from './index'
import type { Vehicle } from '@/types'

export const getVehicles = () => {
  return api.get('/vehicles')
}

export const getVehicle = (id: number) => {
  return api.get(`/vehicles/${id}`)
}

export const createVehicle = (data: Omit<Vehicle, 'id'>) => {
  return api.post('/vehicles', data)
}

export const updateVehicle = (id: number, data: Partial<Vehicle>) => {
  return api.put(`/vehicles/${id}`, data)
}

export const deleteVehicle = (id: number) => {
  return api.delete(`/vehicles/${id}`)
}