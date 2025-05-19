import { defineStore } from 'pinia'
import { login, register } from '@/api/auth'
import type { LoginRequest } from '@/api/auth'
import type { User } from '@/types'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token'),
    user: null as User | null
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token
  },
  
  actions: {
    async login(data: LoginRequest) {
      const response = await login(data)
      const token = response.data.token
      localStorage.setItem('token', token)
      this.token = token
    },
    
    async register(data: LoginRequest) {
      await register(data)
    },
    
    logout() {
      localStorage.removeItem('token')
      this.token = null
      this.user = null
    }
  }
})