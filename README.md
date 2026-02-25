# Device Inventory Service

## Project Overview

A RESTful service for managing device inventory. It supports creating, updating (PUT/PATCH), retrieving, filtering, paginating, sorting, and deleting devices while enforcing domain-level business rules.

Each device contains:

- **Id** (UUID)
- **Name**
- **Brand**
- **State** (`AVAILABLE`, `IN_USE`, `INACTIVE`)
- **Creation Time**

The application is containerized with Docker Compose for consistent local and production-ready execution.

---

## Key Features

- Create devices
- Full updates (PUT)
- Partial updates (PATCH)
- Retrieve single or multiple devices
- Filter by brand and state
- Pagination and sorting
- Domain-validated deletion
- Structured global error handling
- OpenAPI documentation (Swagger UI)
- Unit and integration tests

---

## Domain Rules

- `creationTime` is immutable
- `name` and `brand` cannot be modified when state is `IN_USE`
- Devices in `IN_USE` state cannot be deleted

Business rules are enforced inside the domain entity to guarantee consistency regardless of entry point.

---

## Architecture

The application follows a layered architecture:

- **Controller Layer** – REST endpoints and request/response mapping
- **Service Layer** – Business logic and domain rule enforcement
- **Repository Layer** – Spring Data JPA with Specifications for filtering
- **Domain Layer** – `Device` entity and state-based validation

Additional components:

- Global exception handling
- PostgreSQL (primary database)
- H2 (test database)
- Springdoc OpenAPI

This structure ensures maintainability, testability, and separation between business logic and infrastructure concerns.

---

## Tech Stack

- Java 21
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- PostgreSQL
- H2 (tests)
- Springdoc OpenAPI
- JUnit 5 & Mockito
- Docker & Docker Compose
- Maven

---

## Running the Application

### Local Run

1. Ensure PostgreSQL is running and create the database:

```bash
create db devices
```

### Run with the local profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Git Hooks Setup (Required for Local Push Validation)

This project enforces test execution and JaCoCo coverage checks before every `git push`.

After cloning the repository, run:

```bash
git config core.hooksPath githooks
chmod +x githooks/pre-push
````

#### Application URL:

http://localhost:8181

#### Swagger UI:

http://localhost:8181/swagger-ui/index.html

#### OpenAPI JSON:

http://localhost:8181/v3/api-docs

#### If port 8181 is in use:

```bash
lsof -i :8181
```
```bash
kill -9 <PID>
```

### Docker Run

Build and start containers:
```bash
docker compose up --build
```

#### Access the API:

http://localhost:8090

#### Swagger UI:

http://localhost:8090/swagger-ui/index.html

#### Health check:

http://localhost:8090/actuator/health

#### Stop containers:

```bash
docker compose down
```

#### Remove volumes:

```bash
docker compose down -v
```
#### Port Mapping

In docker-compose.yml:

ports: "8090:8080"

- 8080 → container port

- 8090 → host port

#### The API is accessed via:

http://localhost:8090

### Testing Strategy

#### Unit Tests

- Service layer tested with JUnit 5 and Mockito

- Repository interactions mocked

- Business rules and edge cases validated independently

#### Integration Tests

- Full Spring context

- Real database interaction

- REST endpoints tested

- Pagination and filtering verified

This layered testing approach validates both business logic and end-to-end API behavior.

### Future Improvements

#### Database Migrations (Flyway)
- Introduce version-controlled schema management and remove reliance on ddl-auto.

#### Security Layer
- Add JWT authentication and role-based authorization.

#### Soft Delete Strategy
- Replace hard deletes with deletedAt (and optionally deletedBy) for auditability.

#### Database Indexing
- Add indexes on brand and state to improve query performance.

#### Monitoring & Observability
- Extend Actuator metrics, structured logging, correlation IDs, and monitoring integration.

---