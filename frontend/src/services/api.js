const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8000/api'

async function request(method, endpoint, data = null) {
  
 // toke guardado al hacer login
  const token = localStorage.getItem('token')

  const config = {
    method: method,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }), // adjunta el token si existe
    },
    body: data ? JSON.stringify(data) : null, // solo incluye body si hay datos
  }

  let response

  try {
    response = await fetch(`${BASE_URL}${endpoint}`, config)
  } catch (networkError) {
    throw new Error('No se pudo conectar con el servidor. Comprueba tu conexión.') // sin conexión o CORS
  }

  if (response.status === 401) {
    if (endpoint !== '/login') {
      // sesión expirada — limpia y redirige
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    throw new Error('UNAUTHORIZED')
  }

  // Para errores HTTP intenta parsear el JSON del backend, si no es JSON lanza mensaje genérico
  if (!response.ok) {
    const contentType = response.headers.get('Content-Type') ?? ''
    if (contentType.includes('application/json')) {
      throw await response.json()
    }
    throw new Error(`Error ${response.status}: algo salió mal.`)
  }

  // Respuestas 204 No Content u otras sin body devuelven null
  const text = await response.text()
  return text ? JSON.parse(text) : null
}

export const api = {
  get: (endpoint) => request('GET', endpoint),
  post: (endpoint, data) => request('POST', endpoint, data),
  put: (endpoint, data) => request('PUT', endpoint, data),
  delete: (endpoint) => request('DELETE', endpoint),
}

export default BASE_URL
