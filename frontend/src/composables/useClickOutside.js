import { onMounted, onUnmounted } from 'vue'

// Composable reutilizable — detecta clicks fuera de un elemento del DOM
// elementRef → ref del elemento a vigilar
// callback   → función a ejecutar cuando el usuario haga clic fuera
export function useClickOutside(elementRef, callback) {

    const handler = (e) => {
        // e.target → elemento exacto donde hizo clic el usuario
        // Valida si e.target está dentro de elementRef o si está fuera
        if (elementRef.value && !elementRef.value.contains(e.target)) {
            callback() 
        }
    }

    onMounted(() => document.addEventListener('click', handler))

    onUnmounted(() => document.removeEventListener('click', handler))
}