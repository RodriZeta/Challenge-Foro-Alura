# Challenge-Foro-Alura
Repositorio con Challenge final de Alura con Back-End de Foro

[![Java](https://img.shields.io/badge/Java-21-informational)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Flyway](https://img.shields.io/badge/Flyway-11-red)](https://flywaydb.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-orange)](https://maven.apache.org/)
<!-- Si agregas CI, descomenta la siguiente línea y ajusta owner/repo y workflow name
[![Build](https://github.com/magdalenamiralles/foro-api/actions/workflows/build.yml/badge.svg)](https://github.com/magdalenamiralles/foro-api/actions/workflows/build.yml)
-->

API REST del foro con autenticación JWT, control de acceso por roles, migraciones con Flyway y documentación (Swagger si se habilita).  


> Autor(a): **Rodrigo Zúñiga Ampuero**

---

## Tabla de contenidos
- [Arquitectura](#arquitectura)
- [Requisitos](#requisitos)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [Endpoints](#endpoints)
- [Autenticación en Postman](#autenticación-en-postman)
- [Migraciones (Flyway)](#migraciones-flyway)
- [CORS](#cors)
- [Troubleshooting](#troubleshooting)
- [Contribuir](#contribuir)
- [Licencia](#licencia)

---

## Arquitectura

- **Java 21**, **Spring Boot 3.5** (Web, Security, JPA)
- **JWT** para autenticación/autorizar
- **MySQL 8** con **Flyway** para migraciones
- **Controladores REST** bajo `/api/**`
- **Actuator** para healthcheck (`/actuator/health`)
- **CORS** configurado con `allowedOriginPatterns`

Estructura relevante:
```
com.foro
 ├─ config/           # CORS, Actuator info
 ├─ security/         # JWT filter, SecurityConfig, UserDetailsService
 ├─ domain/
 │   ├─ user/         # Entidad User, repositorio
 │   └─ topic/        # Entidad Topic (+ Status converter), repositorio
 ├─ web/
 │   ├─ controller/   # TopicController, AuthController
 │   └─ dto/          # DTOs de Topic y Auth
 └─ resources/
     └─ db/migration/ # Migraciones Flyway V1__, V2__, V3__
```

## Requisitos
- JDK 21
- Maven 3.9+
- MySQL 8.0 (local)
- (Opcional) Docker para levantar MySQL

## Configuración

1. **Variables de entorno (opcional)**  
   Puedes usar el perfil `dev` con `application-dev.properties` (ya incluido):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/foro_dev?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=admin

   spring.jpa.hibernate.ddl-auto=validate
   spring.jpa.show-sql=true

   spring.flyway.enabled=true

   # JWT
   app.jwt.secret=change-me-please-32+chars
   app.jwt.expiration=3600
   ```

2. **Perfil activo**  
   El proyecto se inicia con el perfil `dev`:
   ```bash
   set SPRING_PROFILES_ACTIVE=dev   # Windows CMD
   $env:SPRING_PROFILES_ACTIVE="dev" # PowerShell
   export SPRING_PROFILES_ACTIVE=dev # Bash
   ```

## Ejecución

```bash
# Clonar
git clone https://github.com/magdalenamiralles/foro-api.git
cd foro-api

# Compilar
mvn -U clean package

# Ejecutar en dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Si usas Docker para MySQL:
```bash
docker run --name mysql-foro -p 3306:3306 -e MYSQL_ROOT_PASSWORD=admin -e MYSQL_DATABASE=foro_dev -d mysql:8
```

## Endpoints

### Actuator
- `GET /actuator/health` → status UP

### Auth
- `POST /api/auth/register` → crea usuario
  ```json
  { "name":"Maga", "email":"maga@example.com", "password":"Maga12345!" }
  ```
- `POST /api/auth/login` → devuelve JWT
  ```json
  { "email":"maga@example.com", "password":"Maga12345!" }
  ```
  Respuesta (ejemplo):
  ```json
  { "token":"<JWT>" }
  ```

### Tópicos
- `GET /api/topics` → lista paginada de tópicos
- `GET /api/topics/{id}` → **detalle por ID** (usa `@PathVariable` y valida existencia)
- `POST /api/topics` → crea
- `PUT /api/topics/{id}` → **actualiza por ID** (valida existencia, mismas reglas de negocio que creación)
- `DELETE /api/topics/{id}` → **elimina por ID** (`deleteById`)

**Respuesta de detalle de tópico** (campos requeridos):  
`título`, `mensaje`, `fechaDeCreacion`, `estado`, `autor`, `curso` (formato JSON).

> Todos los endpoints bajo `/api/**` (excepto `/api/auth/**`) requieren **Bearer JWT**.

## Autenticación en Postman

1. **Login** → `POST /api/auth/login` con email/password.  
2. Copia el `token` del body de respuesta.
3. En tus requests protegidos, pestaña **Authorization** → **Type: Bearer Token** → pega el token.
4. Prueba `GET /api/topics` y `GET /api/topics/{id}`.

> Si recibes `401`, asegúrate de **no** anteponer `"Bearer "` manualmente si Postman ya lo añade, y que el token no esté vacío.

## Migraciones (Flyway)

Para **reiniciar** la base (cuando cambian las migraciones o hubo fallas):

### PowerShell (Windows)
```powershell
mvn --% -U `
  "-Dflyway.url=jdbc:mysql://localhost:3306/foro_dev?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" `
  "-Dflyway.user=root" `
  "-Dflyway.password=admin" `
  "-Dflyway.cleanDisabled=false" `
  flyway:clean flyway:migrate
```

### Bash (macOS/Linux/WSL)
```bash
mvn -U   "-Dflyway.url=jdbc:mysql://localhost:3306/foro_dev?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"   "-Dflyway.user=root"   "-Dflyway.password=admin"   "-Dflyway.cleanDisabled=false"   flyway:clean flyway:migrate
```

Si Flyway reporta **checksum mismatch** o migración fallida, ejecuta:
```bash
mvn -U flyway:repair flyway:migrate
```

## CORS

Se usa `allowedOriginPatterns` para permitir orígenes con credenciales.  
Ejemplo (ya aplicado en `CorsConfig`):
```java
config.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
config.setAllowCredentials(true);
```
> Evita `allowedOrigins("*")` cuando `allowCredentials=true` (Spring lo bloqueará).

## Troubleshooting

- **401 Unauthorized**: faltan headers `Authorization: Bearer <JWT>` o token inválido.
- **CORS 500** con _allowCredentials_: usa `allowedOriginPatterns` en lugar de `allowedOrigins("*")`.
- **MySQL driver**: `Cannot load driver class com.mysql.cj.jdbc.Driver` → revisa `mysql-connector-j` en `pom.xml` y credenciales.
- **Flyway**:
  - _Found more than one migration with version X_: renombra versiones (V1__, V2__, V3__…).
  - _Table already exists_: usa `flyway:clean` seguido de `flyway:migrate`.
  - _Checksum mismatch_: `flyway:repair` y volver a migrar.
- **Bean ambiguo en SecurityConfig (HandlerMappingIntrospector)**: inyecta por tipo o con `@Qualifier` (`CorsConfigurationSource`), o añade `@Primary` a tu bean de CORS.
- **PowerShell interpreta -D***: usa `--%` o comillas como se muestra arriba.

## Contribuir
1. Crea un fork
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -m "feat: agrega X"`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

## Licencia
MIT © 2025 **Rodrigo Zúñiga Ampuero**
