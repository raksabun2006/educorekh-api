# EduCoreKH - High School Management System API

EduCoreKH is a Spring Boot High School Management System backend providing REST APIs for user authentication, admission processing, academic year management, class scheduling, student enrollment, attendance tracking, and grading.

---

## Tech Stack

- **Framework:** Spring Boot 4.1.1
- **Language:** Java 25
- **Build Tool:** Gradle (Wrapper 9.7.1)
- **Database:** PostgreSQL (Spring Data JPA / Hibernate)
- **Security:** Spring Security + Stateless JWT
- **API Documentation:** OpenAPI 3 / Swagger UI (`springdoc-openapi-starter-webmvc-ui`)
- **Monitoring & Metrics:** Spring Boot Actuator (`/actuator/health`, `/actuator/info`)
- **Containerization:** Multi-stage Dockerfile (Eclipse Temurin JDK/JRE 25)

---

## Local Development

### 1. Prerequisites
- Java 25
- Docker & Docker Compose (or local PostgreSQL 17 on port 5416)

### 2. Start PostgreSQL
```bash
docker compose up -d
```

### 3. Run the Backend
```bash
./gradlew bootRun
```
The application starts by default on `http://localhost:8080`.

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **Actuator Health:** `http://localhost:8080/actuator/health`

---

## Deploy to Railway

Follow these steps to deploy EduCoreKH to [Railway](https://railway.com):

### 1. Create Railway Project
1. Log in to your [Railway Dashboard](https://railway.com/dashboard).
2. Click **New Project** -> **Deploy from GitHub repo**.
3. Select your `school` (EduCoreKH) repository.

### 2. Add PostgreSQL Database Service
1. In your Railway project canvas, click **Create** / **New** -> **Database** -> **Add PostgreSQL**.
2. Railway will spin up a managed PostgreSQL database instance.

### 3. Configure Environment Variables
In your backend service settings on Railway, navigate to the **Variables** tab and set the following environment variables:

| Variable | Value / Description | Example |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `prod` |
| `DB_URL` | PostgreSQL JDBC Connection URL | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}` |
| `DB_USERNAME` | Database username | `${{Postgres.PGUSER}}` |
| `DB_PASSWORD` | Database password | `${{Postgres.PGPASSWORD}}` |
| `JWT_SECRET` | Secret key for JWT token signature (>= 256 bits / 32 chars) | `your-secure-random-jwt-secret-key-min-32-chars-long` |
| `JWT_EXPIRATION` | JWT token expiration time in milliseconds (Default: `86400000` = 24h) | `86400000` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed frontend origins | `https://your-frontend-domain.com,http://localhost:5173` |

> **Note on Railway Port:** Railway automatically injects the `PORT` environment variable during deployment, which Spring Boot reads dynamically (`server.port: ${PORT:8080}`). You do not need to manually configure `PORT`.

### 4. Deploy Dockerfile
Railway detects the `Dockerfile` at the root of the repository automatically and builds the multi-stage image.
- Build stage: Eclipse Temurin JDK 25 runs `./gradlew bootJar -x test` to generate `app.jar`.
- Runtime stage: Eclipse Temurin JRE 25 runs `java -jar app.jar` with a non-root system user.

### 5. Generate Railway Domain
1. In your backend service on Railway, navigate to **Settings** -> **Networking** -> **Public Networking**.
2. Click **Generate Domain** (e.g. `https://educorekh-production.up.railway.app`).

### 6. Verify Health Endpoint & Swagger UI
Once deployed, verify your API endpoints:
- **Health Check:**
  ```http
  GET https://YOUR-APP.up.railway.app/actuator/health
  ```
  Expected Response:
  ```json
  {"status":"UP"}
  ```

- **Swagger UI Documentation:**
  ```http
  GET https://YOUR-APP.up.railway.app/swagger-ui/index.html
  ```

---

## API Verification Flow

### 1. Register & Login (Authentication)
- **Register:** `POST /api/v1/auth/register`
  ```json
  {
    "fullName": "Bun Raksa",
    "email": "raksa@example.com",
    "password": "Password123!",
    "sex": "MALE"
  }
  ```
- **Login:** `POST /api/v1/auth/login`
  ```json
  {
    "email": "raksa@example.com",
    "password": "Password123!"
  }
  ```
  Response returns `{ "token": "<JWT_ACCESS_TOKEN>" }`.

- **Get Profile:** `GET /api/v1/auth/me`
  ```http
  Authorization: Bearer <JWT_ACCESS_TOKEN>
  ```

### 2. School Management Lifecycle
1. **Academic Year:** `POST /api/v1/academic-years` -> `PATCH /api/v1/academic-years/{id}/activate`
2. **Class:** `POST /api/v1/classes` (links to academic year ID and grade level)
3. **Admission:** `POST /api/v1/admissions` -> `PATCH /api/v1/admissions/{id}/review` -> `POST /api/v1/admissions/{id}/approve`
4. **Student & Enrollment:** Auto-created student from approved admission -> `POST /api/v1/enrollments`
5. **Attendance & Grading:** `POST /api/v1/attendance` and `POST /api/v1/grades`
# educorekh-api
