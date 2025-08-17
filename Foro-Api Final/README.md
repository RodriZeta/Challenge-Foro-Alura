# Foro API

API de foro con Spring Boot 3, JWT, JPA/Hibernate, Flyway y MySQL.

## Requisitos
- Java 21
- Maven 3.9+
- MySQL 8+ (con usuario/password locales)

## Configuración rápida
1. Crea base de datos local:
   ```sql
   CREATE DATABASE foro_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Ajusta credenciales en `src/main/resources/application-dev.properties` (por defecto `root/admin`).

3. Migraciones de base con Flyway (opcional; la app lo hace al arrancar):  
   ```bash
   mvn --% "-Dflyway.url=jdbc:mysql://localhost:3306/foro_dev" "-Dflyway.user=root" "-Dflyway.password=admin" flyway:migrate
   ```

4. Ejecuta la app:  
   ```bash
   mvn spring-boot:run
   ```
   o desde IntelliJ con el perfil `dev`.

## Endpoints principales

- **Auth**
  - `POST /api/auth/register` — registro `{name,email,password}` → `{token}`
  - `POST /api/auth/login` — login `{email,password}` → `{token}`

- **Tópicos** (requieren `Authorization: Bearer <token>`)
  - `GET /api/topics` — lista de tópicos
  - `GET /api/topics/{id}` — detalle por id
  - `POST /api/topics` — crea `{title,message,courseId}`
  - `PUT /api/topics/{id}` — actualiza `{title,message,status}`
  - `DELETE /api/topics/{id}` — elimina

## Health
- `GET /actuator/health` → UP

## Notas
- CORS abierto (allowedOriginPatterns="*") + allowCredentials(true).
- Flyway crea tablas base en `V1__init.sql` y datos de ejemplo en `V3__seed_roles_cursos.sql`.
