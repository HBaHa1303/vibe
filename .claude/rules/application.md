---
paths:
  - "./src/**/application/**"
---

# Application Layer Rules

## General

- Contains UseCases (Command and Query handlers)
- Strict CQRS separation

---

## CQRS Separation

### Command Side

- Depends on domain layer (repository ports, entities, value objects)
- Orchestrates domain logic through domain entities
- Manages transaction boundaries (`@Transactional`)

### Query Side

- Depends on infrastructure layer directly (JPA repositories)
- Bypasses domain layer entirely
- Read-optimized: maps JPA entities directly to response DTOs
- Domain is only concerned with invariants, business rules, aggregates
- Queries have no business rules, so no need for domain layer

---

## Command

- Annotated with `@Service` and `@Transactional`
- Changes state
- Must NOT return domain entities
- Returns void or result type (e.g., UUID)
- Depends on domain repository port (NOT JPA repository directly)

Naming: `<Verb><Aggregate>CommandService`

Example:
```java
@Service
@RequiredArgsConstructor
public class CreateUserCommandService {
    private final UserRepository userRepository; // domain port

    @Transactional
    public UUID execute(CreateUserCommand command) {
        // construct VOs, check preconditions, call domain factory, save
    }
}
```

---

## Query

- Annotated with `@Service` and `@Transactional(readOnly = true)`
- Read-only
- Must NOT modify state
- Returns DTO or projection only
- Uses JPA repository directly (NOT domain repository port)
- Maps JPA entity fields directly to DTO fields (flat mapping, no VOs)
- Contains a private `toResponse()` helper for JPA entity -> DTO mapping

Naming: `<Aggregate>QueryService`

Example:
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {
    private final UserJpaRepository userJpaRepository; // direct JPA access

    public UserResponse getUserById(UUID id) {
        var entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("..."));
        return toResponse(entity);
    }

    private UserResponse toResponse(UserJpaEntity entity) {
        return new UserResponse(entity.getId(), entity.getUsername(), ...);
    }
}
```

---

## Command Objects

- Use Java records
- Named: `<Verb><Aggregate>Command`
- Contain raw values (String, UUID), NOT Value Objects

Examples:
- `CreateUserCommand(String username, String email, String role, ...)`
- `UpdateUserCommand(UUID userId, String username, String email, ...)`

---

## Response DTOs

- Use Java records
- Named: `<Aggregate>Response`, `<Aggregate>PageResponse`
- Flat structure with primitive/standard types only

Examples:
- `UserResponse(UUID id, String username, String email, ...)`
- `UserPageResponse(List<UserResponse> content, int page, ...)`

---

## Dependency Rules

### Command Side
- CommandService -> Domain Repository Port -> RepositoryAdapter -> JPA
- Must NOT import from infrastructure directly

### Query Side
- QueryService -> JPA Repository directly
- Must NOT import from domain layer
- Must NOT use domain entities or value objects

---

## Must

- Command side: construct Value Objects, check preconditions, use domain factory methods
- Query side: use JPA repository directly, map flat fields to DTOs
- Return DTOs only (never domain entities)
- Keep Command and Query completely separate

---

## Must NOT

- Contain business logic (business rules belong in domain)
- Command side: import from infrastructure directly
- Query side: import from domain layer
- Mix command and query concerns
- Return domain entities or JPA entities

---

# Enforcement

- No business rules in CommandService or QueryService
- CommandService must use domain repository port, NOT JPA repository
- QueryService must use JPA repository, NOT domain repository port
- No domain entities in return types
- No mixing of command and query dependencies
