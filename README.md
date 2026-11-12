# Huellas Dulces

API REST para la gestión de productos, inventario, usuarios y perfiles de una tienda de mascotas.

Desarrollada con Java 25 y Spring Boot 4, con arquitectura por capas e integración con un frontend Angular 21.

## Tecnologías utilizadas

- Java 25 y Spring Boot 4.
- Spring Web MVC y Spring Security.
- JWT (JSON Web Token).
- Spring Data JPA e Hibernate.
- H2 y PostgreSQL.
- MongoDB y Redis.
- Flyway y MapStruct.
- Resilience4j y Spring RestClient.
- Spring Boot Actuator.
- WebSocket y STOMP.
- Maven y Docker.

## Arquitectura del proyecto

El backend utiliza una arquitectura por capas para separar responsabilidades y facilitar el mantenimiento.

Paquetes principales:

- `config`: configuración de seguridad, JWT, CORS, WebSocket, caché y OpenAPI.
- `controller`: controladores REST de autenticación, productos, catálogo externo y perfiles.
- `document`: documentos de auditoría.
- `dto`: objetos de transferencia de datos.
- `exception`: manejo de excepciones.
- `mapper`: transformación de objetos.
- `model`: entidades del dominio.
- `repository`: acceso a datos.
- `service`: lógica de negocio.

## Funcionalidades principales

### Gestión de productos e inventario

- Crear, consultar, actualizar y eliminar productos.
- Consultar categorías.
- Controlar existencias.
- Registrar retiros de inventario.
- Validar disponibilidad de stock.

### Seguridad y autenticación

- Inicio de sesión mediante JWT.
- Protección de endpoints con Spring Security.
- Autorización basada en roles.
- Roles de administrador y usuario.
- Contraseñas protegidas mediante BCrypt.
- Sesiones sin estado (stateless).
- Configuración CORS restringida al origen `http://localhost:4200`.

### Persistencia y auditoría

- Persistencia relacional mediante Spring Data JPA.
- H2 para desarrollo.
- PostgreSQL para producción.
- MongoDB para auditoría.
- Auditoría de creación, modificación y movimientos de inventario.
- Registro automático de fechas y usuarios responsables.

### Caché

- Caché de productos y categorías.
- SimpleCacheManager para desarrollo.
- Redis para producción.
- Invalidación de caché al modificar el inventario.

### Integración externa y resiliencia

- Consulta de OpenPetFoodFacts mediante RestClient.
- Circuit Breaker con Resilience4j.
- Tiempo límite de respuesta configurado.
- Mecanismo de fallback cuando el servicio externo no está disponible.

### Perfil y avatares

El backend permite administrar la información del perfil del usuario y sus imágenes:

- Consultar y actualizar el perfil.
- Recibir imágenes mediante `MultipartFile`.
- Validar el formato WebP.
- Limitar el tamaño del avatar a 1 MB.
- Almacenar los datos de imagen y su tipo de contenido.
- Consultar y eliminar el avatar.

El procesamiento y redimensionamiento de la imagen se realiza en el frontend antes de enviarla al backend.

### Comunicación en tiempo real

La aplicación incorpora configuración de WebSocket con STOMP:

- Habilitación del soporte de mensajería.
- Registro de endpoints STOMP.
- Configuración de orígenes permitidos.
- Integración con el frontend Angular para funcionalidades de tiempo real.

### Monitoreo y rendimiento

- Spring Boot Actuator.
- Endpoint de salud.
- Métricas de aplicación.
- Métricas personalizadas de conflictos de inventario.
- Métricas de eventos de fallback.
- Configuración de Virtual Threads.

## Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/auth/login` | Inicio de sesión |
| GET | `/api/v1/products` | Consultar productos |
| GET | `/api/v1/products/{id}` | Consultar producto |
| GET | `/api/v1/products/categories` | Consultar categorías |
| POST | `/api/v1/products` | Crear producto |
| PUT | `/api/v1/products/{id}` | Actualizar producto |
| DELETE | `/api/v1/products/{id}` | Eliminar producto |
| POST | `/api/v1/products/{id}/withdraw` | Retirar inventario |
| GET | `/api/v1/external-catalog/search` | Consultar catálogo externo |
| GET | `/api/v1/profile/me` | Consultar perfil |
| PUT | `/api/v1/profile/me` | Actualizar perfil |
| PUT | `/api/v1/profile/me/avatar` | Subir avatar |
| GET | `/api/v1/profile/me/avatar` | Consultar avatar |
| DELETE | `/api/v1/profile/me/avatar` | Eliminar avatar |
| GET | `/actuator/health` | Estado de la aplicación |
| GET | `/actuator/metrics` | Métricas |

Los endpoints protegidos requieren autenticación y los permisos correspondientes.

## Configuración CORS y WebSocket

El backend restringe los orígenes HTTP y WebSocket al frontend local:

`http://localhost:4200`

La configuración se encuentra en `CorsConfig.java`, `SecurityConfig.java` y `WebSocketConfig.java`.

## Ejecución del proyecto

### Requisitos

- Java 25.
- Maven.
- Git.
- Visual Studio Code o IntelliJ IDEA.
- Docker (opcional).

### Ejecutar en Windows

```bash
mvnw.cmd spring-boot:run
```

Ejecutar las pruebas:

```bash
mvnw.cmd clean test
```

### Documentación API

Con la aplicación iniciada:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

## Docker

El proyecto incluye configuración para ejecutar la aplicación y servicios de infraestructura mediante Docker Compose.

```bash
docker compose up --build
```

Se deben configurar las variables de entorno requeridas antes de utilizar los perfiles correspondientes.

## Perfiles de configuración

- `dev`: desarrollo con H2 y caché en memoria.
- `prod`: producción con PostgreSQL, MongoDB y Redis.

Las credenciales y secretos deben suministrarse mediante variables de entorno.

## Flujo de integración frontend y backend

1. El usuario inicia sesión desde Angular.
2. El frontend envía las credenciales al endpoint de autenticación.
3. El backend valida las credenciales y genera un JWT.
4. Angular adjunta el token Bearer a las solicitudes protegidas.
5. Spring Security valida el acceso a los recursos.
6. Los controladores reciben las solicitudes y delegan la lógica a los servicios.
7. Los repositorios administran la persistencia de la información.
8. WebSocket y STOMP permiten la comunicación en tiempo real.
9. El perfil recibe y almacena los avatares procesados por el frontend.

## Control de versiones

El proyecto utiliza Git y GitHub para mantener el historial de cambios.

Repositorio backend:

https://github.com/Adrian-hub-cell/Huellas-Dulces-Backend

## Autor

Proyecto académico Huellas Dulces.
Desarrollado con Java 25 y Spring Boot 4.

## Pruebas automatizadas y cobertura

El backend utiliza JUnit 5, AssertJ, Mockito, Spring Boot Test y Testcontainers.

### Tipos de pruebas

- **Pruebas unitarias:** servicios, modelos, controladores, excepciones y mapeadores.
- **Pruebas de seguridad:** MockMvc y `@WithMockUser`.
- **Pruebas de integración:** migraciones Flyway con PostgreSQL y Testcontainers.
- **Pruebas WebSocket:** autenticación JWT en conexiones STOMP.

### Ejecución

Ejecutar `mvnw.cmd clean verify` para ejecutar las pruebas y generar el informe JaCoCo.

### Resultados de cobertura JaCoCo

| Métrica | Cobertura | Objetivo |
|---|---:|---:|
| Instructions | 92.93 % | 80 % |
| Branches | 80.36 % | 80 % |
| Lines | 93.07 % | 80 % |
| Complexity | 84.27 % | 80 % |
| Methods | 88.63 % | 80 % |
| Classes | 100 % | 80 % |

Todas las métricas alcanzan o superan el objetivo mínimo del 80 %.

### Informes

- HTML: `target/site/jacoco/index.html`
- XML: `target/site/jacoco/jacoco.xml`
