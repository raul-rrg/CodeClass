<template>
  <div class="flex flex-col h-screen overflow-hidden">
    <Navbar />
    <main class="flex-1 min-h-0 overflow-auto" style="scrollbar-gutter: stable">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
import { useAuthStore } from './stores/auth';


const authStore = useAuthStore()

// Si el usuario ya está autenticado, intenta obtener su información al cargar la app
if (authStore.isAuthenticated) {
  authStore.fetchUser().catch((error) => {
    if (error.message === 'UNAUTHORIZED') authStore.logout() // solo cierra sesión si token inválido, no por error de red
  })
}


</script>
