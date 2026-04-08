import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: () => import('@/views/Home.vue'), meta: { requiresGuest: true } },
    { path: '/challenges', name: 'challenges', component: () => import('@/views/Challenges.vue') },
    { path: '/ranking', name: 'ranking', component: () => import('@/views/Ranking.vue') },
    { path: '/about', name: 'about', component: () => import('@/views/About.vue') },
    { path: '/login', name: 'login', component: () => import('@/views/Login.vue'), meta: { requiresGuest: true } },    // solo sin sesión
    { path: '/register', name: 'register', component: () => import('@/views/Register.vue'), meta: { requiresGuest: true } }, // solo sin sesión
    { path: '/profile', name: 'profile', component: () => import('@/views/Profile.vue'), meta: { requiresAuth: true } },    // requiere sesión
  ],
})

// Guard global: redirige según el estado de autenticación
router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isAuthenticated) return { name: 'login' } // no autenticado → login
  if (to.meta.requiresGuest && auth.isAuthenticated) return { name: 'home' }  // ya autenticado → home
})

export default router
