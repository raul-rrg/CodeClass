<div align="center">
  <br />

  <img src="logo.svg" alt="CodeClass logo" width="220" height="220" />

  <br /><br />

  <p>Plataforma multiplataforma de ejercicios de programación para entornos educativos</p>

  <p>
    <img src="https://img.shields.io/badge/Laravel-12-FF2D20?style=flat-square&logo=laravel&logoColor=white" alt="Laravel" />
    <img src="https://img.shields.io/badge/Vue-3-42b883?style=flat-square&logo=vuedotjs&logoColor=white" alt="Vue 3" />
    <img src="https://img.shields.io/badge/Tailwind-v4-38bdf8?style=flat-square&logo=tailwindcss&logoColor=white" alt="Tailwind" />
    <img src="https://img.shields.io/badge/PHP-8.4-777BB4?style=flat-square&logo=php&logoColor=white" alt="PHP" />
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
  - [2. Configurar variables de entorno](#2-configurar-variables-de-entorno)
  - [3. Arrancar con Docker](#3-arrancar-con-docker)
  - [Desarrollo local sin Docker](#desarrollo-local-sin-docker)
- [Despliegue de Judge0 CE](#despliegue-de-judge0-ce)
- [API — Referencia rápida](#api--referencia-rápida)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Licencia](#licencia)

---

## Sobre el proyecto

CodeClass es una plataforma diseñada para el aprendizaje de programación en entornos educativos con dos roles diferenciados:

- **Profesores** — crean y publican ejercicios con casos de prueba automatizados, gestionan torneos y consultan estadísticas detalladas de resolución.
- **Alumnos** — resuelven los ejercicios en el navegador o desde la app Android, reciben feedback inmediato sobre su código gracias a la integración con [Judge0](https://judge0.com/) y compiten en torneos.

La plataforma soporta tres lenguajes de programación (**Python, Java y JavaScript**) y cuatro niveles de dificultad (**fácil, medio, difícil e insano**). Los enunciados pueden traducirse automáticamente mediante la API de DeepL.

Además, CodeClass cuenta con una **app Android** (Kotlin + Jetpack Compose) que permite tanto consultar ejercicios y rankings como **crear ejercicios** directamente desde el móvil.

> Proyecto de Trabajo de Fin de Grado (TFG).

---

## Características

- Autenticación con roles: **profesor** y **alumno**
- CRUD completo de ejercicios con casos de prueba (públicos y ocultos)
- Editor de código en el navegador (Monaco Editor) con evaluación automática via Judge0
- Soporte para **Python**, **Java** y **JavaScript**
- Cuatro niveles de dificultad: **fácil**, **medio**, **difícil**, **insano**
- Categorías de ejercicios: arrays, cadenas, matemáticas, recursión, ordenación, árboles, grafos, programación dinámica
- **Sistema de torneos** — competencias públicas o privadas con código de acceso, leaderboard propio y estados (próximo / activo / finalizado)
- **Traducción automática** de enunciados con DeepL (español ↔ inglés)
- Historial de entregas detallado por alumno y por ejercicio
- **Estadísticas** de resolución por ejercicio (solo profesores)
- **Ranking global** de usuarios
- App Android para consultar ejercicios, historial, ranking **y crear ejercicios**
- Evaluación asíncrona mediante **queue worker** (Laravel Queue + base de datos)
- Interfaz moderna y responsiva con soporte i18n

---

## Stack tecnológico

| Capa | Tecnología |
|------|------------|
| Backend | Laravel 12, PHP 8.4 |
| Autenticación | Laravel Sanctum (Bearer token) |
| Evaluación de código | Judge0 CE |
| Traducción | DeepL API |
| Cola de trabajos | Laravel Queue (driver: database) |
| Frontend web | Vue 3.5, Vite 7, Pinia, Vue Router 5, Vue i18n |
| Editor web | Monaco Editor |
| Estilos | Tailwind CSS v4 |
| App móvil | Kotlin, Jetpack Compose, Material 3 |
| Editor móvil | Sora Editor |
| Base de datos | MySQL 8.0 |

---

## Primeros pasos

### Requisitos previos

- Docker y Docker Compose
- Instancia de [Judge0 CE](https://github.com/judge0/judge0) accesible desde la máquina donde se despliega (ver [sección de despliegue](#despliegue-de-judge0-ce))
- *(Opcional)* Clave de API de [DeepL](https://www.deepl.com/pro-api) para traducción automática de enunciados

### 1. Clonar el repositorio

```bash
git clone https://github.com/raul-rrg/CodeClass.git
cd CodeClass
```

### 2. Configurar variables de entorno

Abre `docker-compose.yml` y ajusta las variables del servicio `backend`:

```yaml
JUDGE0_URL: http://<IP_DE_TU_JUDGE0>:2358
DEEPL_API_KEY: <tu_clave_deepl>   # opcional, solo si quieres traducción automática
```

> **Importante:** `JUDGE0_URL` debe apuntar a la IP de la máquina donde corre Judge0. Si la IP cambia, actualiza esta línea y reinicia los contenedores.

### 3. Arrancar con Docker

```bash
docker compose up -d
```

Esto levanta cuatro servicios:

| Servicio | URL / Puerto | Descripción |
|----------|-------------|-------------|
| Backend (API) | http://localhost:8000 | Laravel API REST |
| Frontend | http://localhost:5173 | Vue 3 SPA |
| Base de datos | MySQL en puerto 3306 (interno) | MySQL 8.0 |
| Queue Worker | — | Proceso que evalúa entregas en background |

### Desarrollo local sin Docker

```bash
cd backend
cp .env.example .env          # Ajustar DB_*, JUDGE0_URL y DEEPL_API_KEY
composer install
php artisan key:generate
php artisan migrate --seed
php artisan serve             # → http://localhost:8000

# En otra terminal, arrancar el worker de cola
php artisan queue:work
```

```bash
cd frontend
npm install
npm run dev                   # → http://localhost:5173
```

---

## Despliegue de Judge0 CE

Judge0 requiere una VPS con acceso root completo (no funciona en PaaS). Probado oficialmente solo en **Ubuntu 22.04**.

### Requisitos
- Ubuntu 22.04, mínimo 2 GB RAM
- Docker y Docker Compose instalados

### 1. Configurar GRUB (obligatorio)

Judge0 necesita cgroups v1. En Ubuntu 22.04 hay que forzarlo:

```bash
sudo nano /etc/default/grub
# Añadir en GRUB_CMDLINE_LINUX:
# systemd.unified_cgroup_hierarchy=0

sudo update-grub && sudo reboot
```

### 2. Desplegar

```bash
wget https://github.com/judge0/judge0/releases/download/v1.13.1/judge0-v1.13.1.zip
unzip judge0-v1.13.1.zip && cd judge0-v1.13.1

# Editar judge0.conf y establecer REDIS_PASSWORD y POSTGRES_PASSWORD
# Generador: https://www.random.org/passwords/?num=2&len=32&format=plain&rnd=new

docker compose up -d db redis && sleep 10 && docker compose up -d
```

Judge0 quedará disponible en `http://<IP>:2358`.

### 3. Conectar con CodeClass

```env
JUDGE0_URL=http://<IP_JUDGE0>:2358
```

Documentación oficial: https://github.com/judge0/judge0#installation

---

## API — Referencia rápida

**Base URL:** `http://localhost:8000/api/v1`

> Todas las rutas protegidas requieren el header `Authorization: Bearer {token}`.

### Autenticación

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| `POST` | `/register` | Público | Registro de usuario |
| `POST` | `/login` | Público | Inicio de sesión |
| `POST` | `/logout` | Autenticado | Cerrar sesión |
| `GET` | `/users/me` | Autenticado | Perfil del usuario |
| `PUT` | `/users/me` | Autenticado | Actualizar perfil |
| `POST` | `/users/me/avatar` | Autenticado | Subir avatar |

### Ejercicios

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| `GET` | `/exercises` | Público | Listar ejercicios (filtros: `difficulty`, `category`, `search`, `solved`) |
| `GET` | `/exercises/{id}` | Autenticado | Ver ejercicio |
| `POST` | `/exercises` | Profesor | Crear ejercicio |
| `PUT` | `/exercises/{id}` | Profesor | Editar ejercicio |
| `DELETE` | `/exercises/{id}` | Profesor | Eliminar ejercicio |
| `GET` | `/users/me/exercises` | Profesor | Mis ejercicios |
| `GET` | `/users/me/exercises/stats` | Profesor | Estadísticas globales de mis ejercicios |
| `GET` | `/exercises/{id}/detail-stats` | Profesor | Estadísticas detalladas de un ejercicio |

### Entregas

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| `POST` | `/exercises/{id}/run` | Autenticado | Ejecutar código sin guardar (solo test públicos) |
| `POST` | `/exercises/{id}/submissions` | Autenticado | Enviar solución (evaluación asíncrona) |
| `GET` | `/exercises/{id}/submissions` | Autenticado | Entregas de un ejercicio |
| `GET` | `/submissions/{id}` | Autenticado | Ver entrega |
| `GET` | `/users/me/submissions` | Autenticado | Mi historial de entregas |

### Torneos

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| `GET` | `/tournaments` | Público | Listar torneos (filtro: `status=upcoming\|active\|finished`) |
| `GET` | `/tournaments/{id}` | Público | Ver torneo |
| `GET` | `/tournaments/code/{code}` | Autenticado | Buscar torneo por código de acceso |
| `POST` | `/tournaments` | Profesor | Crear torneo |
| `POST` | `/tournaments/{id}/join` | Autenticado | Unirse a torneo |
| `GET` | `/tournaments/{id}/leaderboard` | Autenticado | Leaderboard del torneo |

### Ranking

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| `GET` | `/leaderboard` | Público | Ranking global de usuarios |

---

## Estructura del proyecto

```
CodeClass/
├── backend/                  # API REST — Laravel 12 / PHP 8.4
│   ├── app/
│   │   ├── Http/Controllers/Api/V1/
│   │   │   ├── AuthController.php
│   │   │   ├── ExerciseController.php
│   │   │   ├── SubmissionController.php
│   │   │   ├── RunController.php
│   │   │   ├── TournamentController.php
│   │   │   └── LeaderboardController.php
│   │   ├── Models/           # User, Exercise, TestCase, Submission,
│   │   │                     # SubmissionResult, Tournament
│   │   ├── Jobs/
│   │   │   ├── EvaluateSubmission.php
│   │   │   └── TranslateExerciseJob.php
│   │   └── Enums/            # UserRole, Difficulty, ProgrammingLanguage,
│   │                         # SubmissionStatus
│   ├── database/
│   └── routes/api.php
├── frontend/                 # SPA — Vue 3 + Vite
│   └── src/
│       ├── views/            # Landing, Challenges, Challenge, Tournaments,
│       │                     # Tournament, CreateExercise, CreateTournament,
│       │                     # MyExercises, Statistics, Profile, Progress,
│       │                     # Ranking, About
│       ├── components/
│       ├── stores/           # Pinia (auth, editorPreferences)
│       ├── services/         # API client
│       ├── router/
│       └── locales/          # i18n (es, en)
└── mobile/                   # App Android — Kotlin + Jetpack Compose
    └── app/src/main/java/com/codeclass/app/
        ├── data/             # Retrofit, ApiService, DataStore
        ├── ui/screens/       # auth, challenges, exercises, profile,
        │                     # ranking, submissions
        └── viewmodel/        # 8 ViewModels
```

---

## Licencia

Copyright (c) 2026 **Raúl Rivera Garrido**. Todos los derechos reservados.

Queda prohibida la reproducción, distribución o modificación de este software sin el consentimiento expreso del autor. Consulta el archivo [LICENSE](LICENSE) para más información.
