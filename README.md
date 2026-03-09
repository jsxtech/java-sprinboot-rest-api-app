# Java Spring Boot Application

Production-ready Spring Boot REST API application with advanced features.

## Features

### Core Framework
- Spring Boot 3.2.3
- Java 17
- Maven build system

### Data & Persistence
- JPA/Hibernate ORM
- H2 in-memory database
- Spring Data JPA repositories
- Custom query methods
- Unique constraints on email
- Transaction management with @Transactional
- Automatic schema generation

### REST API
- RESTful CRUD operations
- Pagination and sorting support
- Bean Validation (JSR-380)
- Global exception handling
- Proper HTTP status codes (200, 201, 204, 400, 404)
- Request/Response validation

### Performance & Caching
- Spring Cache abstraction
- Targeted cache invalidation
- Read-only transaction optimization

### Documentation
- OpenAPI 3.0 specification
- Swagger UI for interactive API testing
- API endpoint descriptions and tags

### Monitoring & Operations
- Spring Boot Actuator
- Health checks
- Application metrics
- H2 Console for database inspection

### Architecture
- Layered architecture (Controller → Service → Repository)
- Service layer with business logic
- Constructor-based dependency injection
- Separation of concerns

### Logging
- SLF4J structured logging
- Privacy-compliant (no PII in logs)
- Configurable log levels

### DevOps
- Docker support with multi-stage builds
- Docker Compose orchestration
- Sample data initialization
- Environment-based configuration

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker (optional)

## Running the Application

### Using Maven
```bash
mvn spring-boot:run
```

### Using Maven Wrapper
```bash
./mvnw spring-boot:run
```

### Using Docker
```bash
docker build -t javasbapp .
docker run -p 8080:8080 javasbapp
```

### Using Docker Compose
```bash
docker-compose up --build
```

## API Endpoints

### Basic
- `GET /` - Returns "Hello, Spring Boot!"

### User Management
- `GET /api/users` - Get all users with pagination
  - Query params: `page`, `size`, `sort` (e.g., `?page=0&size=10&sort=name,asc`)
  - Default page size: 10
- `GET /api/users/{id}` - Get user by ID (cached)
  - Returns: 200 OK or 404 Not Found
- `GET /api/users/search?name={name}` - Search users by name (case-insensitive)
- `GET /api/users/email/{email}` - Get user by email
  - Returns: 200 OK or 404 Not Found
- `POST /api/users` - Create new user
  - Validates: name (required), email (required, valid format, unique)
  - Returns: 201 Created
- `PUT /api/users/{id}` - Update user
  - Validates: name (required), email (required, valid format)
  - Returns: 200 OK or 404 Not Found
- `DELETE /api/users/{id}` - Delete user
  - Returns: 204 No Content

### API Documentation
- `GET /swagger-ui.html` - Interactive API documentation
- `GET /api-docs` - OpenAPI specification (JSON)

### Monitoring
- `GET /actuator/health` - Health check endpoint
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Database Console
- `http://localhost:8080/h2-console` - H2 database console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (leave empty)

## Sample Data

The application initializes with 2 sample users:
- John Doe (john@example.com)
- Jane Smith (jane@example.com)

## Configuration

Server runs on port 8080 by default. Configuration in `src/main/resources/application.properties`:

```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update
management.endpoints.web.exposure.include=health,info,metrics
```

## Project Structure

```
javasbapp/
├── src/main/java/com/example/
│   ├── Application.java              # Main Spring Boot application
│   ├── HelloController.java          # Basic hello endpoint
│   ├── User.java                     # User entity with validation
│   ├── UserRepository.java           # JPA repository with custom queries
│   ├── UserService.java              # Service layer with transactions
│   ├── UserController.java           # REST controller with caching
│   ├── GlobalExceptionHandler.java   # Global exception handling
│   ├── OpenApiConfig.java            # Swagger/OpenAPI configuration
│   └── DataInitializer.java          # Sample data loader
├── src/main/resources/
│   └── application.properties        # Application configuration
├── Dockerfile                        # Multi-stage Docker build
├── docker-compose.yml                # Docker Compose setup
├── pom.xml                           # Maven dependencies
└── README.md                         # This file
```

## Technology Stack

- **Framework**: Spring Boot 3.2.3
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: H2 (in-memory)
- **ORM**: Hibernate/JPA
- **Validation**: Bean Validation (Hibernate Validator)
- **Caching**: Spring Cache
- **Documentation**: SpringDoc OpenAPI
- **Monitoring**: Spring Boot Actuator
- **Logging**: SLF4J/Logback
- **Containerization**: Docker

## Best Practices Implemented

- ✅ Layered architecture
- ✅ Constructor injection
- ✅ Transaction management
- ✅ Input validation
- ✅ Exception handling
- ✅ Caching strategy
- ✅ API documentation
- ✅ Logging without PII
- ✅ Proper HTTP status codes
- ✅ Pagination support
- ✅ Docker containerization
