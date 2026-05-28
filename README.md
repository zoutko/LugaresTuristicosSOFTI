# LugaresTuristicosSOFTI

Aplicacion full-stack para consultar y administrar lugares y recorridos turisticos. El proyecto esta dividido en un backend REST con Spring Boot y un frontend web con Angular.

## Arquitectura

| Capa | Tecnologia | Ubicacion |
| --- | --- | --- |
| Backend | Java 17, Spring Boot 4, Spring Data JPA, Spring Security, JWT | `backend/` |
| Frontend | Angular 20, TypeScript, RxJS | `frontend/` |
| Base de datos local | H2 en memoria | `backend/src/main/resources/application.yml` |
| Datos iniciales | Script SQL cargado al iniciar | `backend/src/main/resources/data.sql` |

## Requisitos

- Java 17.
- Maven 3.9+.
- Node.js compatible con Angular 20. Se recomienda Node.js 20.19+ o 22.12+.
- npm.
- Docker, solo si se quiere construir la imagen del backend.

No es necesario instalar Angular CLI globalmente: el proyecto lo incluye como dependencia de desarrollo y puede ejecutarse con `npm start` o `npx ng`.

## Configuracion

El backend usa valores por defecto para desarrollo local, pero permite sobreescribir variables por entorno:

| Variable | Valor por defecto | Uso |
| --- | --- | --- |
| `PORT` | `8080` | Puerto HTTP del backend. |
| `JWT_SECRET` | definido en `application.yml` | Clave para firmar tokens JWT. En produccion debe cambiarse. |
| `MAIL_PASSWORD` | definido en `application.yml` | Contrasena usada por el servicio de correo. En produccion debe venir del entorno. |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Origenes permitidos para consumir la API. |

El frontend tiene configurado `frontend/proxy.conf.json` para redirigir `/api` hacia `http://localhost:8080` durante desarrollo.

## Ejecucion local

### 1. Backend

Desde la raiz del repositorio:

```powershell
cd backend
mvn spring-boot:run
```

En Linux/macOS:

```bash
cd backend
./mvnw spring-boot:run
```

El backend queda disponible en `http://localhost:8080`.

### 2. Frontend

En otra terminal:

```powershell
cd frontend
npm install
npm start
```

La aplicacion queda disponible en `http://localhost:4200`. Las llamadas a `/api` se envian automaticamente al backend local por medio del proxy.

## Pruebas y build

### Backend

```powershell
cd backend
mvn test
```

### Frontend

```powershell
cd frontend
npm test
```

Build de produccion:

```powershell
cd frontend
npm run build
```

El resultado se genera en `frontend/dist/frontend/browser`.

## API principal

Todas las rutas del backend estan bajo `/api`.

| Recurso | Rutas base |
| --- | --- |
| Autenticacion | `/api/auth` |
| Usuarios | `/api/users` |
| Categorias | `/api/categories` |
| Lugares turisticos | `/api/places` |
| Multimedia de lugares | `/api/places/{placeId}/media` |
| Tours | `/api/tours` |
| Multimedia de tours | `/api/tours/{tourId}/media` |
| Reseñas de tours | `/api/tours/{tourId}/reviews` |

## Base de datos

En desarrollo el backend usa H2 en memoria:

- URL JDBC: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Consola H2: `http://localhost:8080/h2-console`
- Script inicial: `backend/src/main/resources/data.sql`

La base se recrea al iniciar la aplicacion (`ddl-auto: create-drop`), por lo que los datos manuales no persisten entre ejecuciones.

## Despliegue

### Frontend en Netlify

El archivo `netlify.toml` configura:

- base: `frontend`
- comando: `npm install && ng build`
- publish: `dist/frontend/browser`
- proxy `/api/*` hacia `https://gestor-recorridos-backend.onrender.com/api/:splat`

### Backend con Docker

```powershell
cd backend
docker build -t lugares-backend .
docker run -p 8080:8080 -e JWT_SECRET=change-me -e MAIL_PASSWORD=change-me lugares-backend
```

El `Dockerfile` compila con Maven y ejecuta el JAR usando Eclipse Temurin 17.

## Estructura del proyecto

```text
.
|-- backend/
|   |-- src/main/java/com/proyecto/app/   # Codigo Spring Boot
|   |-- src/main/resources/               # application.yml y data.sql
|   |-- src/test/java/                    # Pruebas unitarias
|   |-- Dockerfile
|   `-- pom.xml
|-- frontend/
|   |-- src/app/                          # Aplicacion Angular
|   |-- public/                           # Assets publicos
|   |-- proxy.conf.json                   # Proxy local hacia el backend
|   |-- angular.json
|   `-- package.json
|-- netlify.toml
`-- README.md
```
