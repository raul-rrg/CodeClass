import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/services/api'

// Store de autenticación global
export const useAuthStore = defineStore('auth', () => {
  const user = ref(null) // usuario autenticado
  const token = ref(localStorage.getItem('token')) // token de Sanctum (persiste en localStorage)

  const isAuthenticated = computed(() => !!token.value) // indica si el usuario está autenticado (token existe)

  async function login(credentials) {
    try {
      const response = await api.post('/login', credentials)

      token.value = response.token
      user.value = response.user

      localStorage.setItem('token', token.value) 
    } catch (error) {
      throw error
    }
  }

  async function register(creedentials) {
    try {
      const response = await api.post('/register', creedentials)

      token.value = response.token
      user.value = response.user

      localStorage.setItem('token', token.value) 
    } catch (error) {
      throw error
    }
  }

  async function logout() {
    try {
      await api.post('/logout')
    } catch (error) {
      throw error
    } finally {
      token.value = null
      user.value = null
      localStorage.removeItem('token') 
    }
  }

  async function fetchUser() {
    try {
      const response = await api.get('/me') 
      user.value = response
    } catch (error) {
      throw error
    }
  }

  return { user, token, isAuthenticated, login, register, logout, fetchUser }
})
