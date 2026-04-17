# Code Style Rules

## General

- Clean, readable, production-ready code
- SOLID principles, prefer composition over inheritance
- Single responsibility per class and method
- Methods: short, focused, max 2-3 nesting levels

---

## Multi-Module Project

- Root `build.gradle.kts`: shared config (plugins, Java version, repositories)
- Sub-module `build.gradle.kts`: service-specific dependencies only
- Common module: pure Java library, no Spring Boot plugin
- Service modules: Spring Boot apps, each runs independently
- MapStruct + Lombok: keep `lombok-mapstruct-binding` in annotation processors

---

## Immutability

- Prefer immutable objects
- Value Objects: final class, final fields, `@Getter`
- Use Java records for DTOs, Commands, Results

---

## Null Handling

- Avoid returning null, use Optional or explicit handling
- Guard clauses at method start for null checks
- Domain `create()`: validate required fields, allow optional (phone, address, avatar)
- Domain `reconstitute()`: only guard `id` via `Objects.requireNonNull`

---

## Error Handling

- Domain exceptions extend `DomainException` from common
- `@RestControllerAdvice` returns `ProblemDetail` (RFC 7807)
- Fields: `type`, `title`, `status`, `detail`, `instance`
- Do not swallow exceptions, provide meaningful messages

---

## Mapping

- Always separate three models: Domain, Persistence (JPA), Presentation (DTO)
- MapStruct `@Mapper(componentModel = "spring")` for all mappers
- Use `default` methods for mappings MapStruct cannot auto-generate
- Use `@Named` + `qualifiedByName` for custom Value Object conversions
- Keep `lombok-mapstruct-binding` in annotation processors

---

## Dependency Rules

- Direction: presentation → application → domain
- Infrastructure → domain/application via interfaces
- Domain: NO dependencies on any other layer
- Service modules depend on common module

---

## CQRS

- Separate command and query logic
- CommandService: `@Transactional`, modifies state
- QueryService: `@Transactional(readOnly = true)`, read-only

---

## Anti-Patterns (MUST NOT)

- Business logic in controller, repository, or infrastructure
- Returning entity directly from API
- `Impl` suffix (use `Adapter`)
- Using repository in controller
