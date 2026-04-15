---
paths:
  - "**/domain/**"
---

# Domain Layer Rules

## General

- Must be pure Java (no Spring, no JPA, no framework dependencies)
- Must NOT use annotations (except basic Java)
- Must NOT import from common module's non-domain packages

---

## Contains

- Entities (with identity)
- Value Objects (immutable)
- Domain Exceptions
- Repository interfaces (ports)
- Enums

---

## Aggregate Root (Entity)

- Must encapsulate behavior (no anemic model)
- Must protect invariants
- Two static factory methods:
    - `create(...)` - for new entities, validates inputs, sets defaults (id, timestamps, isActive)
    - `reconstitute(...)` - for rebuilding from persistence, no validation
- Private constructor (called by factory methods only)
- Behavior methods instead of setters (e.g., `updateProfile()`, `deactivate()`)
- All mutations update `updatedAt` timestamp
- `equals`/`hashCode` based on identity (`id`) only

Example:
```java
public class User {
    private User(...) {} // private constructor

    public static User create(Username username, Email email, ...) {
        // validates, sets defaults, returns new instance
    }

    public static User reconstitute(UUID id, Username username, ...) {
        // no validation, used by infrastructure
    }

    public void updateProfile(...) { /* behavior */ }
    public void deactivate() { /* behavior */ }
}
```

---

## Value Objects

- Must be immutable (final class, final fields, no setters)
- Private constructor
- Two factory methods:
    - `of(String value)` - validates input, throws domain exception on invalid
    - `reconstitute(String value)` - no validation, for infrastructure mapping from trusted storage
- `equals`/`hashCode` based on value
- `toString()` returns the raw value

Example:
```java
public final class Email {
    private final String value;

    private Email(String value) { this.value = value; }

    public static Email of(String value) {
        // validate, throw InvalidEmailException if invalid
    }

    public static Email reconstitute(String value) {
        return new Email(value); // no validation
    }
}
```

---

## Repository Interface (Port)

- Pure Java interface, no Spring annotations
- Methods return domain entities or `Optional<DomainEntity>`
- Use `PageResult<T>` from common module for pagination (NOT Spring's Page)
- Uniqueness check methods (e.g., `existsByEmail`)

Example:
```java
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    PageResult<User> findAll(int page, int size);
    boolean existsByEmail(String email);
}
```

---

## Domain Exceptions

- Extend base exception from common module (`DomainException`)
- Hierarchy per domain: `<Domain>DomainException` -> specific exceptions
- Meaningful error messages

Example:
```
DomainException (common)
  -> UserDomainException
     -> InvalidEmailException
     -> InvalidUsernameException
```

---

## Must NOT

- Access database
- Call external services
- Use repository implementations (only port interfaces)
- Import from application, infrastructure, or presentation layers
- Use Spring or JPA annotations

---

## Can

- Define repository interfaces (ports)
- Use other domain objects
- Use common module's base classes (`DomainException`, `PageResult`)

---

# Enforcement

- No @Entity, @Service, @Repository in domain
- No dependency to infrastructure, application, or presentation layers
- No framework imports in domain
