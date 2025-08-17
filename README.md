# Challenge-Foro-Alura
Repositorio con Challenge final de Alura con Back-End de Foro

[![Java](https://img.shields.io/badge/Java-21-007396?logo=java)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-6DB33F?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)](https://www.mysql.com/)
[![Flyway](https://img.shields.io/badge/Flyway-11.x-CC0200?logo=flyway)](https://flywaydb.org/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apache-maven)](https://maven.apache.org/)

---

## 🚀 Descripción

API REST para gestionar un foro con **usuarios, tópicos y respuestas**, con autenticación **JWT**, documentación **OpenAPI/Swagger** y migraciones de base de datos con **Flyway**. Pensada para ser ejecutada localmente y desplegada fácilmente.

---

## 🧱 Stack

- **Lenguaje:** Java 21  
- **Framework:** Spring Boot 3.5.x (Web, Security, Data JPA, Validation, Actuator)  
- **Base de datos:** MySQL 8.0  
- **ORM:** Hibernate / JPA  
- **Migraciones:** Flyway  
- **Build:** Maven

---

## 🗺️ Arquitectura & Módulos

- `com.foro.domain` – Entidades JPA, repositorios y value objects.  
- `com.foro.web` – Controladores REST y DTOs.  
- `com.foro.security` – Filtro JWT, configuración de seguridad y servicios de autenticación.  
- `com.foro.config` – Configuraciones transversales (CORS, Actuator, Jackson).

---

## 🔐 Autenticación (JWT)

- **Registro:** `POST /api/auth/register` → crea usuario (name, email, password).  
- **Login:** `POST /api/auth/login` → devuelve `token` (Bearer).  
- **Uso:** Enviar `Authorization: Bearer <jwt>` en cada request protegido.

> Los endpoints públicos: `/api/auth/**`, `/actuator/health`, `/actuator/info`, `/swagger-ui/**`, `/v3/api-docs/**`.

---

## 🌐 CORS

Configurado para permitir **credenciales** y **orígenes específicos** (no `*` cuando `allowCredentials=true`).  
Edita `com/foro/config/CorsConfig.java` para ajustar `allowedOriginPatterns` según tu front (por ej. `http://localhost:5173`).

---

## 🛣️ Endpoints principales

### 📚 Tópicos
- **Listar todos** – `GET /api/topics`
- **Detalle** – `GET /api/topics/{id}`
- **Crear** – `POST /api/topics`
- **Actualizar** – `PUT /api/topics/{id}`
- **Eliminar** – `DELETE /api/topics/{id}`

**Respuesta (JSON) incluye:** `title`, `message`, `createdAt`, `status`, `author`, `course`.

> Se usa `@PathVariable Long id` y se valida existencia con `Optional.isPresent()`; si no existe, retorna **404**.

### 💬 Respuestas
- **Listar por tópico** – `GET /api/topics/{id}/replies`
- **Crear respuesta** – `POST /api/topics/{id}/replies`

---

## 🧩 Reglas de negocio cumplidas

- Requerido **ID** en detalle/actualización/eliminación de un tópico.  
- Validación de existencia antes de actualizar o eliminar.  
- Mismas validaciones de **crear** aplican a **actualizar**.  
- Respuestas en **JSON** con campos solicitados.

---

## 🧪 Colección Postman

Incluye:
- Variables de entorno (`baseUrl`, `jwt`).
- Scripts automáticos para **capturar el JWT** tras login y guardarlo en `{{jwt}}`.
- Requests para **health**, **auth**, **topics** y **replies**.

> Importa `postman/ForoAPI.postman_collection.json` y `postman/ForoAPI.postman_environment.json` (si están en el repo).

---

## ⚙️ Variables de entorno

Crea un archivo `application-dev.properties` o usa variables del sistema:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/foro_dev?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=admin

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true

# JWT
app.security.jwt.secret=change-me-super-secret
app.security.jwt.expiration=86400000
```

> Perfil activo recomendado: `dev` (`spring.profiles.active=dev`).

---

## 🗄️ Base de datos & Migraciones (Flyway)

### 📜 Orden de migraciones
- `V1__init.sql` – Usuarios, perfiles, cursos, relaciones base.  
- `V2__topics_replies.sql` – Tablas `topics` y `replies`.  
- `V3__seed_roles_cursos.sql` – Datos semilla (roles y cursos).  
- `V4__seed_users_topics.sql` – Datos semilla (usuarios y tópicos).

### 🧹 Limpiar y migrar (PowerShell)
**Importante en Windows PowerShell:** usa `--%` para evitar que PowerShell interprete los `-D`:

```powershell
mvn --% -U `
  "-Dflyway.url=jdbc:mysql://localhost:3306/foro_dev?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" `
  "-Dflyway.user=root" `
  "-Dflyway.password=admin" `
  "-Dflyway.cleanDisabled=false" `
  flyway:clean flyway:migrate
```

### 🛠️ Reparar historial (checksums/órdenes)
```bash
mvn -U flyway:repair
```

---

## ▶️ Ejecutar localmente

```bash
# 1) Compilar
mvn -U clean package

# 2) Levantar la app
mvn spring-boot:run

# 3) Swagger/OpenAPI
http://localhost:8080/swagger-ui/index.html
```

> Health check: `GET http://localhost:8080/actuator/health` → **200 UP**.

---

## 🧰 Observabilidad (Actuator) & Logging

- **Actuator:** `/actuator/health`, `/actuator/info`.  
- **Logs:** Configurados para Spring Security, request mapping y JPA (nivel DEBUG en perfil dev si se desea).

---

## 🗂️ Estructura del proyecto

```
src/main/java/com/foro
├── config/           # CORS, Actuator, Jackson
├── security/         # JWT, SecurityConfig, filtros
├── domain/           # entidades, repos, converters, enums
│   └── topic/        # Topic, Reply, DTOs & mappers
└── web/              # controllers y dtos
src/main/resources
├── application.properties / application-dev.properties
└── db/migration      # V1__... V2__... V3__... V4__...
```

---

## 🧭 Perfiles

- `dev` – Desarrollo local (MySQL local, logs verbosos, Swagger habilitado).  
- `prod` – Para despliegue (ajusta CORS, logs, y secretos JWT).

---

## ✅ Pruebas rápidas (cURL)

```bash
# Registro
curl -X POST http://localhost:8080/api/auth/register   -H "Content-Type: application/json"   -d '{"name":"Maga","email":"maga@example.com","password":"Maga12345!"}'

# Login (obtén token)
curl -X POST http://localhost:8080/api/auth/login   -H "Content-Type: application/json"   -d '{"email":"maga@example.com","password":"Maga12345!"}'

# Listar tópicos (usa tu <TOKEN>)
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/topics
```

---

## 🧯 Troubleshooting

- **401 No autorizado:** falta o expira JWT → hacer login y reenviar `Authorization: Bearer <jwt>`.
- **CORS `allowCredentials` + `*`:** usa `allowedOriginPatterns` con orígenes explícitos (no `*`).  
- **Flyway checksum mismatch / versiones duplicadas:** `mvn flyway:repair` y volver a migrar.  
- **Tablas ya existen / orden de migraciones:** ejecuta `flyway:clean` (solo en entorno local) y `flyway:migrate`.

---

## 🗺️ Roadmap

- 🔄 Refresh tokens  
- 🧵 Paginación y filtros avanzados en `/api/topics`  
- 📝 Auditoría (createdBy/updatedBy)  
- 🧪 Tests de integración con Testcontainers

---

## 🤝 Contribuir

1. Haz un fork  
2. Crea una rama (`feat/mi-cambio`)  
3. Commit & push  
4. Pull Request 🙌

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT**.

---

## 🧠 Autor

Desarrollado por **Rodrigo Zúñiga** como parte del programa **Oracle Next Education (ONE) - Alura LATAM**.
