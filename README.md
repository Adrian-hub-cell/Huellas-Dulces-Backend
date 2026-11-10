# 🐾 Huellas Dulces

**API REST para la gestión de productos e inventario de Huellas Dulces.**

Huellas Dulces es un proyecto desarrollado con Java y Spring Boot, orientado a la administración de productos, control de inventario, autenticación de usuarios y almacenamiento de información.

## 🚀 Tecnologías utilizadas

- Java 25
- Spring Boot 4
- Spring Web MVC
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- Hibernate
- H2 Database
- PostgreSQL
- MongoDB
- Redis
- Flyway
- MapStruct
- Resilience4j
- Spring RestClient
- Spring Boot Actuator
- Maven
- Docker

## 🏗️ Arquitectura del proyecto

El proyecto utiliza una **arquitectura basada en capas**, que permite separar las responsabilidades, mejorar la organización del código y facilitar su mantenimiento y escalabilidad.

### Estructura principal

```text
src/main/java/com/huellasdulces
│
├── config/
│   ├── CacheConfig.java
│   ├── JpaAuditingConfig.java
│   ├── JwtConfig.java
│   ├── MongoAuditingConfig.java
│   ├── OpenApiConfig.java
│   ├── ProductMapperConfig.java
│   └── SecurityConfig.java
│
├── controller/
│   ├── AuthController.java
│   ├── ExternalCatalogController.java
│   └── ProductController.java
│
├── document/
│   └── ProductAuditDocument.java
│
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── ProductRequest.java
│   ├── ProductResponse.java
│   └── StockRequest.java
│
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ProductNotFoundException.java
│   └── StockConflictException.java
│
├── mapper/
│   └── ProductMapper.java
│
├── model/
│   ├── Customer.java
│   ├── Order.java
│   ├── OrderStatus.java
│   ├── Product.java
│   └── Supplier.java
│
├── repository/
│   ├── ProductAuditRepository.java
│   └── ProductRepository.java
│
├── service/
│   ├── AuthService.java
│   ├── ExternalCatalogService.java
│   ├── ProductService.java
│   └── ProductServiceImpl.java
│
└── HuellasDulcesApplication.java
```

### Recursos y configuraciones

```text
src/main/resources
│
├── db/
│   └── migration/
│       ├── V1__create_initial_schema.sql
│       ├── V2__create_suppliers_customers_orders.sql
│       └── V3__add_product_audit_fields.sql
│
├── application.properties
├── application-dev.properties
└── application-prod.properties
```

## ⚙️ Funcionalidades principales

### Gestión de productos
- Crear productos.
- Consultar todos los productos.
- Consultar productos por identificador.
- Actualizar información de productos.
- Eliminar productos.
- Consultar categorías.
- Controlar las existencias del inventario.
- Registrar retiros de inventario con validación de stock.

### Seguridad y autenticación
- Autenticación mediante JWT.
- Protección de endpoints con Spring Security.
- Autorización basada en roles.
- Roles de administrador y usuario.
- Contraseñas protegidas mediante BCrypt.
- Configuración de sesiones sin estado (stateless).

### Persistencia y auditoría
- Persistencia relacional mediante Spring Data JPA.
- Configuración de PostgreSQL para producción.
- Base de datos H2 para desarrollo.
- Auditoría de operaciones mediante MongoDB.
- Registro de creación, modificación y movimientos de inventario.
- Auditoría automática de fechas y usuarios responsables.

### Caché
- Caché de productos y categorías.
- SimpleCacheManager para desarrollo.
- Redis para el perfil de producción.
- Invalidación de caché al modificar el inventario.

### Integración externa y resiliencia
- Integración con OpenPetFoodFacts mediante RestClient.
- Configuración de Circuit Breaker con Resilience4j.
- Tiempo límite de respuesta de 1.5 segundos.
- Mecanismo de fallback cuando el servicio externo no está disponible.
- Mensaje de validación manual requerido por Huellas Dulces.

### Monitoreo y rendimiento
- Spring Boot Actuator.
- Endpoint de salud de la aplicación.
- Métricas personalizadas de conflictos de inventario.
- Métricas de eventos de fallback.
- Habilitación de Virtual Threads.

## 🔗 Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/auth/login` | Iniciar sesión |
| GET | `/api/v1/products` | Consultar productos |
| GET | `/api/v1/products/{id}` | Consultar producto |
| GET | `/api/v1/products/categories` | Consultar categorías |
| POST | `/api/v1/products` | Crear producto |
| PUT | `/api/v1/products/{id}` | Actualizar producto |
| DELETE | `/api/v1/products/{id}` | Eliminar producto |
| POST | `/api/v1/products/{id}/withdraw` | Retirar inventario |
| GET | `/api/v1/external-catalog/search` | Consultar catálogo externo |
| GET | `/actuator/health` | Estado de la aplicación |
| GET | `/actuator/metrics` | Métricas de la aplicación |

**Nota:** Los endpoints protegidos requieren autenticación y los permisos correspondientes.

## 🧪 Ejecución del proyecto

### Requisitos
- Java 25.
- Maven.
- Visual Studio Code o IntelliJ IDEA.
- Git.

### Ejecutar en desarrollo

En Windows:

```bash
mvnw.cmd spring-boot:run
```

También se puede ejecutar mediante:

```bash
mvnw.cmd clean test
```

### Documentación API

Con la aplicación iniciada:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

## 🐳 Docker

El proyecto incluye:

- `Dockerfile`: construcción de la imagen de la aplicación.
- `docker-compose.yml`: configuración de los servicios de la aplicación, PostgreSQL, MongoDB y Redis.

Para iniciar los servicios:

```bash
docker compose up --build
```

La ejecución requiere tener Docker instalado y configurar las variables de entorno necesarias.

## 🔐 Perfiles de configuración

El proyecto cuenta con dos perfiles:

- **dev:** configuración de desarrollo con H2 y caché en memoria.
- **prod:** configuración para producción con PostgreSQL, MongoDB y Redis.

Las credenciales y secretos de producción deben suministrarse mediante variables de entorno.

## 📦 Control de versiones

El proyecto utiliza Git y GitHub para el control de versiones y el seguimiento de los avances.

Repositorio:

https://github.com/Adrian-hub-cell/Huellas-Dulces

## 👨‍💻 Autor

**Desarrollado por:** Adrian Gonzalez Sanchez.

Proyecto académico Huellas Dulces, desarrollado con Java y Spring Boot.