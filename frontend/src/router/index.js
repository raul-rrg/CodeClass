import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'landing', component: () => import('@/views/Landing.vue'), meta: { requiresGuest: true } },
    { path: '/home', redirect: '/challenges' },
    { path: '/challenges', name: 'challenges', component: () => import('@/views/Challenges.vue') },
    { path: '/challenges/:id', name: 'challenge', component: () => import('@/views/Challenge.vue'), meta: { requiresAuth: true } },
    { path: '/challenges/:id/submissions', name: 'challenge-submissions', component: () => import('@/views/ChallengeSubmissions.vue'), meta: { requiresAuth: true } },
    { path: '/ranking', name: 'ranking', component: () => import('@/views/Ranking.vue') },
    { path: '/about', name: 'about', component: () => import('@/views/About.vue') },
    { path: '/login', name: 'login', component: () => import('@/views/Login.vue'), meta: { requiresGuest: true } },    // solo sin sesión
    { path: '/register', name: 'register', component: () => import('@/views/Register.vue'), meta: { requiresGuest: true } }, // solo sin sesión
    { path: '/profile', name: 'profile', component: () => import('@/views/Profile.vue'), meta: { requiresAuth: true } },
    { path: '/mis-retos', name: 'my-exercises', component: () => import('@/views/MyExercises.vue'), meta: { requiresAuth: true, requiresTeacher: true } },
    { path: '/estadisticas', name: 'statistics', component: () => import('@/views/Statistics.vue'), meta: { requiresAuth: true, requiresTeacher: true } },
    { path: '/exercises/create', name: 'exercise-create', component: () => import('@/views/CreateExercise.vue'), meta: { requiresAuth: true, requiresTeacher: true } },
    { path: '/exercises/:id/edit', name: 'exercise-edit', component: () => import('@/views/CreateExercise.vue'), meta: { requiresAuth: true, requiresTeacher: true } },
  ],
})

// Guard global: redirige según el estado de autenticación
router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isAuthenticated) return { name: 'login' }
  if (to.meta.requiresGuest && auth.isAuthenticated) return { name: 'challenges' }
  if (to.meta.requiresTeacher && auth.user && !auth.isTeacher) return { name: 'challenges' }
})

export default router
