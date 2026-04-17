---
paths:
  - "./src/**/domain/**"
---

# Domain Layer Rules

## General

- Must be pure Java (no Spring, no JPA)
- Allowed: Lombok (`@Getter`), basic Java annotations
- Must NOT import from application, infrastructure, or presentation layers

---

## Contains

- Aggregate Roots (entities with identity)
- Value Objects (immutable, validated)
- Domain Exceptions
- Repository interfaces (ports)
- Enums

---

## Aggregate Root

- `@Getter` (Lombok) for accessors, no manual getters
- Private constructor, called by factory methods only
- `create(...)` — validates inputs, sets defaults (id, timestamps, isActive)
- `reconstitute(...)` — rebuilds from persistence, only guards `id` via `Objects.requireNonNull`
- Behavior methods instead of setters (e.g., `updateProfile()`, `deactivate()`)
- All mutations update `updatedAt` timestamp
- `equals`/`hashCode` based on identity (`id`) only
- Null protection: guard required fields in `create()`, optional fields (phone, address, avatar) allow null

---

## Value Object

- `@Getter`, final class, final fields, private constructor
- `of(value)` — validates, throws domain exception on invalid
- `reconstitute(value)` — no validation, for infrastructure mapping
- `equals`/`hashCode` based on value, `toString()` returns raw value

---

## Repository Interface (Port)

- Pure Java interface, no Spring annotations
- Return domain entities or `Optional`
- Use `PageResult<T>` from common for pagination

---

## Domain Exceptions

- Extend `DomainException` from common module
- Hierarchy: `DomainException` → `<Domain>DomainException` → specific exceptions
- Meaningful error messages

---

## Must NOT

- Access database or external services
- Use repository implementations (only port interfaces)
- Import from other layers
- Use Spring or JPA annotations
