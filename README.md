# LugaresTuristicosSOFTI

> Aplicación full-stack para gestión y visualización de lugares turísticos.

## Resumen

Proyecto con backend en Java (Spring Boot) y frontend en Angular. Incluye API REST, UI responsiva y datos iniciales en [backend/src/main/resources/data.sql](backend/src/main/resources/data.sql#L1).

## Arquitectura

- **Backend:** Java + Spring Boot (carpeta `backend/`).
- **Frontend:** Angular (carpeta `frontend/`).
- **Base de datos:** configurada en `backend/src/main/resources/application.yml` (ver configuración).

## Requisitos

- Java 11+ (o versión requerida por el `pom.xml`)
- Maven (o usar el wrapper `mvnw` / `mvnw.cmd`)
- Node.js 16+ y npm
- Angular CLI (opcional, para desarrollo local)

## Configuración y ejecución

Backend (desde la raíz del repo):

1. Iniciar el backend:

```bash
cd backend
./mvnw spring-boot:run    # Unix/macOS
mvnw.cmd spring-boot:run  # Windows
```

2. Compilar para producción:

```bash
cd backend
./mvnw clean package
```

Frontend:

1. Instalar dependencias y ejecutar en modo desarrollo:

```bash
cd frontend
npm install
npm start    # o `ng serve` si usa Angular CLI
```

2. Generar build de producción:

```bash
cd frontend
npm run build -- --prod
```

## Docker

El backend contiene un `Dockerfile` en [backend/Dockerfile](backend/Dockerfile#L1) para construir una imagen. Ejemplo:

```bash
cd backend
docker build -t lugares-backend .
docker run -p 8080:8080 --env-file .env lugares-backend
```

Ajuste variables de entorno según `application.yml`.

## Pruebas

- Backend (JUnit / Maven):

```bash
cd backend
./mvnw test
```

- Frontend (Karma/Jasmine):

```bash
cd frontend
npm test
```

## Estructura principal

- `backend/` — aplicación Spring Boot, `pom.xml`, `src/main/java` y `src/main/resources`.
- `frontend/` — aplicación Angular, `src/` y `package.json`.
- `netlify.toml` — configuración de despliegue estático (si aplica).

## Contribuir

1. Abrir un issue describiendo el cambio.
2. Crear una rama con prefijo `feature/` o `fix/`.
3. Hacer PR con descripción y pasos para reproducir.

---

Si quieres, puedo añadir badges, ejemplos de llamadas a la API o instrucciones de despliegue continuo. ¿Deseas que los incluya?
