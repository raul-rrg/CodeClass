<template>
  <Navbar />
  <main>
    <RouterView />
  </main>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
import { useAuthStore } from './stores/auth';


const authStore = useAuthStore()

// Si el usuario ya está autenticado, intenta obtener su información al cargar la app
if (authStore.isAuthenticated) {
  authStore.fetchUser().catch(() => authStore.logout()) // token inválido/expirado → cierra sesión
}


</script>
