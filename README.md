<div align="center">
  <br />

  <img src="logo.svg" alt="CodeClass logo" width="220" height="220" />

  <br /><br />

  <p>Plataforma multiplataforma de ejercicios de programación para entornos educativos</p>

  <p>
    <img src="https://img.shields.io/badge/Laravel-12-FF2D20?style=flat-square&logo=laravel&logoColor=white" alt="Laravel" />
    <img src="https://img.shields.io/badge/Vue-3-42b883?style=flat-square&logo=vuedotjs&logoColor=white" alt="Vue 3" />
    <img src="https://img.shields.io/badge/Tailwind-v4-38bdf8?style=flat-square&logo=tailwindcss&logoColor=white" alt="Tailwind" />
    <img src="https://img.shields.io/badge/PHP-8.2-777BB4?style=flat-square&logo=php&logoColor=white" alt="PHP" />
    <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/licencia-All%20Rights%20Reserved-red?style=flat-square" alt="All Rights Reserved" />
  </p>

  <br />
</div>

---

## Tabla de contenidos

- [Tabla de contenidos](#tabla-de-contenidos)
- [Sobre el proyecto](#sobre-el-proyecto)
- [Características](#características)
- [Stack tecnológico](#stack-tecnológico)
- [Primeros pasos](#primeros-pasos)
  - [Requisitos previos](#requisitos-previos)
  - [1. Clonar el repositorio](#1-clonar-el-repositorio)
  - [2. Backend](#2-backend)
  - [3. Frontend](#3-frontend)
- [API — Referencia rápida](#api--referencia-rápida)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Licencia](#licencia)

---

## Sobre el proyecto

CodeClass es una plataforma diseñada para el aprendizaje de programación en entornos educativos con dos roles diferenciados:

- **Profesores** — crean y publican ejercicios con casos de prueba automatizados.
- **Alumnos** — resuelven los ejercicios en el navegador y reciben feedback inmediato sobre su código gracias a la integración con [Judge0](https://judge0.com/).

Además, CodeClass cuenta con una **app Android** (Kotlin + Jetpack Compose) pensada para el seguimiento: consultar ejercicios, revisar el historial de entregas y ver el ranking, sin necesidad de escribir código desde el móvil.

> Proyecto de Trabajo de Fin de Grado (TFG).

---

## Características

- Autenticación con roles: **profesor** y **alumno**
- CRUD completo de ejercicios con casos de prueba (solo profesores)
- Editor de código en el navegador con evaluación automática via Judge0
- Historial de entregas por alumno
- App Android para consulta y seguimiento
- Interfaz moderna y responsiva

---

## Stack tecnológico

| Capa | Tecnología |
|------|------------|
| Backend | Laravel 12, PHP 8.2 |
| Autenticación | Laravel Sanctum (Bearer token) |
| Frontend web | Vue 3, Vite, Pinia, Vue Router |
| Estilos | Tailwind CSS v4 |
| App móvil | Kotlin, Jetpack Compose |
| Evaluación de código | Judge0 |

---

## Primeros pasos

### Requisitos previos

- PHP 8.2+ y Composer
- Node.js 18+ y npm
- MySQL o PostgreSQL
- Instancia de [Judge0](https://github.com/judge0/judge0)

### 1. Clonar el repositorio

```bash
git clone https://github.com/raul-rrg/CodeClass.git
cd CodeClass
```

### 2. Backend

```bash
cd backend
cp .env.example .env          # Configurar DB_*, JUDGE0_HOST, JUDGE0_TOKEN
composer install
php artisan key:generate
php artisan migrate --seed
php artisan serve             # → http://localhost:8000
```

### 3. Frontend

```bash
cd frontend
npm install
npm run dev                   # → http://localhost:5173
```

---

## API — Referencia rápida

**Base URL:** `http://localhost:8000/api/v1`

> Todas las rutas protegidas requieren el header `Authorization: Bearer {token}`.

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| `POST` | `/register` | Público | Registro de usuario |
| `POST` | `/login` | Público | Inicio de sesión |
| `POST` | `/logout` | Autenticado | Cerrar sesión |
| `GET` | `/me` | Autenticado | Perfil del usuario |
| `GET` | `/exercises` | Autenticado | Listar ejercicios |
| `GET` | `/exercises/{id}` | Autenticado | Ver ejercicio |
| `POST` | `/exercises` | Profesor | Crear ejercicio |
| `PUT` | `/exercises/{id}` | Profesor | Editar ejercicio |
| `DELETE` | `/exercises/{id}` | Profesor | Eliminar ejercicio |
| `POST` | `/submissions` | Alumno | Enviar solución |
| `GET` | `/submissions` | Alumno | Mis entregas |
| `GET` | `/submissions/{id}` | Alumno | Ver entrega |

---

## Estructura del proyecto

```
CodeClass/
├── backend/                  # API REST — Laravel
│   ├── app/
│   │   ├── Http/Controllers/
│   │   ├── Models/
│   │   └── Jobs/             # EvaluateSubmission (Judge0)
│   ├── database/
│   └── routes/api.php
└── frontend/                 # SPA — Vue 3
    └── src/
        ├── components/
        ├── views/
        ├── stores/           # Pinia
        ├── services/         # API client
        └── router/
```

---

## Licencia

Copyright (c) 2026 **Raúl Rivera Garrido**. Todos los derechos reservados.

Queda prohibida la reproducción, distribución o modificación de este software sin el consentimiento expreso del autor. Consulta el archivo [LICENSE](LICENSE) para más información.
