import axios from 'axios'

export interface LoginRequest {
  username: string
  password: string
}

// API base URL
const API_URL = 'http://localhost:8080/api/auth'

export const login = async (data: LoginRequest) => {
  try {
    return await axios.post(`${API_URL}/login`, data)
  } catch (error: any) {
    console.error('Login error details:', error.response?.data || error.message)
    throw error
  }
}

export const register = async (data: LoginRequest) => {
  try {
    return await axios.post(`${API_URL}/register`, data)
  } catch (error: any) {
    console.error('Registration error details:', error.response?.data || error.message)
    throw error
  }
}