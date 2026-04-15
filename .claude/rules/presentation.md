---
paths:
  - "./src/**/presentation/**"
---

# Presentation Layer Rules

## General

- Handles HTTP / API layer (Controller)
- Must be thin (no business logic)
- Calls application layer only
- DTOs must be separate from domain models

---

## CQRS Controller Separation

Controllers MUST be split into Command and Query controllers:

- `<Aggregate>CommandController` - handles write operations (POST, PUT, DELETE)
- `<Aggregate>QueryController` - handles read operations (GET)

---

## Package Structure

```
presentation/
├── controller/
│   ├── <Aggregate>CommandController.java
│   └── <Aggregate>QueryController.java
├── dto/
│   ├── Create<Aggregate>Request.java
│   ├── Update<Aggregate>Request.java
│   ├── <Aggregate>DetailResponse.java
│   ├── <Aggregate>SummaryResponse.java
│   └── <Aggregate>PageResponse.java
├── mapper/
│   └── <Aggregate>DtoMapper.java
└── exception/
    └── <Aggregate>ExceptionHandler.java
```

---

## CommandController

- Annotated with `@RestController`, `@RequestMapping("/api/<resources>")`
- Uses `@RequiredArgsConstructor` for constructor injection
- Injects CommandServices and DtoMapper only
- Must NOT inject QueryService

### Endpoints

| Action | Method | Path | Return |
|--------|--------|------|--------|
| Create | POST | `/api/<resources>` | 201 Created + Location header |
| Update | PUT | `/api/<resources>/{id}` | 204 No Content |
| Delete | DELETE | `/api/<resources>/{id}` | 204 No Content |

---

## QueryController

- Annotated with `@RestController`, `@RequestMapping("/api/<resources>")`
- Uses `@RequiredArgsConstructor` for constructor injection
- Injects QueryService and DtoMapper only
- Must NOT inject CommandServices

### Endpoints

| Action | Method | Path | Return |
|--------|--------|------|--------|
| Get by ID | GET | `/api/<resources>/{id}` | 200 OK + DetailResponse |
| List | GET | `/api/<resources>?page=&size=` | 200 OK + PageResponse |

---

## Common Controller Rules

- Use `UUID` for path variable IDs
- Use `@RequestParam(defaultValue = "0")` for pagination
- Create: return `ResponseEntity.created(URI).build()`
- Update/Delete: return `ResponseEntity.noContent().build()`
- Never return domain entities

---

## Request DTOs

- Use Java records
- Contain raw values (String, UUID) only
- Naming: `<Verb><Aggregate>Request`

Examples:
- `CreateUserRequest(String username, String email, String fullName, ...)`
- `UpdateUserRequest(String username, String email, ...)`

---

## Response DTOs

- Use Java records
- Two levels:
    - `DetailResponse` - full fields for single item
    - `SummaryResponse` - subset of fields for list
- `PageResponse` wraps list with pagination metadata
- Naming: `<Aggregate>DetailResponse`, `<Aggregate>SummaryResponse`, `<Aggregate>PageResponse`

---

## DtoMapper

- Annotated with `@Component`
- Maps between presentation DTOs and application commands/responses
- Methods:
    - `toCreateCommand(Request)` -> Command
    - `toUpdateCommand(UUID, Request)` -> Command
    - `toDetailResponse(AppResponse)` -> DetailResponse
    - `toPageResponse(AppPageResponse)` -> PageResponse

---

## ExceptionHandler

- Annotated with `@RestControllerAdvice`
- Catches `DomainException` from common module -> returns 400
- Catches `NoSuchElementException` -> returns 404
- Must NOT import from domain layer (use `DomainException` from common)
- Error response body includes: message, status, timestamp
- Naming: `<Aggregate>ExceptionHandler`

Example:
```java
@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage(), "status", 400, "timestamp", LocalDateTime.now()));
    }
}
```

---

## Must

- Be thin (no business logic)
- Call application layer only
- Use DtoMapper for all conversions
- Separate Command and Query controllers

---

## Must NOT

- Access domain directly
- Access repository directly
- Contain business rules
- Return domain entities
- Import from domain or infrastructure layers
- Mix command and query operations in one controller

---

# Enforcement

- No business logic in controller
- No direct repository usage
- No returning domain entities in API response
- No import from domain or infrastructure layers
- CommandController must NOT reference QueryService
- QueryController must NOT reference CommandServices
