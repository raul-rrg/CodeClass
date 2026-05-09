import { createI18n } from 'vue-i18n'
import es from '../locales/es.json'
import en from '../locales/en.json'

// I18n es un plugin de Vue que nos permite gestionar la traducibilidad de la aplicación. 
// Aquí lo configuramos con los idiomas disponibles y el idioma por defecto.


// Al cargar la aplicación, intentamos recuperar el idioma guardado en localStorage. 
// Si no hay ninguno, usamos 'es' por defecto.
const savedLocale = localStorage.getItem('locale') || 'es'


// Creamos la instancia de i18n con las opciones necesarias.
export default createI18n({
  legacy: false, 
  locale: savedLocale,
  fallbackLocale: 'es',
  messages: { es, en }
})
