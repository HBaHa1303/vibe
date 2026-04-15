# Code Style Rules

## General

- Code must be clean, readable, and production-ready
- Follow SOLID principles
- Prefer composition over inheritance

---

## Multi-Module Project

- Root `build.gradle.kts` contains shared config: plugins, Java version, repositories, subprojects block
- Sub-module `build.gradle.kts` only adds service-specific dependencies
- `settings.gradle.kts` includes all sub-modules
- Common module: pure Java library, no Spring Boot plugin
- Service modules: Spring Boot apps, each runs independently

---

## Class Design

- Each class must have a single responsibility
- Avoid god classes
- Keep classes small and focused

---

## Method Design

- Methods must be short and focused
- One responsibility per method
- Avoid deep nesting (max 2-3 levels)

---

## Immutability

- Prefer immutable objects
- Value Objects must be immutable (final class, final fields, no setters)
- Use Java records for DTOs, Commands, Queries

---

## Null Handling

- Avoid returning null
- Use Optional or explicit handling
- Guard clauses at the start of methods for null checks

---

## Error Handling

- Use domain exceptions for business rule violations
- Base exception `DomainException` in common module
- Each domain extends with specific exceptions (e.g., `UserDomainException`)
- `@RestControllerAdvice` for mapping exceptions to HTTP responses
- Do not swallow exceptions
- Provide meaningful error messages

---

## Logging

- Log only meaningful events
- Do not log sensitive data

---

## Mapping

- Always separate three models:
    - Domain model (entities, value objects)
    - Persistence model (JPA entities)
    - Presentation model (request/response DTOs)

- Use dedicated Mapper classes:
    - `PersistenceMapper` for JPA entity <-> Domain entity
    - `DtoMapper` for presentation DTO <-> application command/response

- No mapping logic in controller or domain

---

## Dependency Rules

- Follow dependency direction:
    - presentation -> application -> domain
    - infrastructure -> domain/application via interfaces
- Service modules depend on common module
- Domain layer has NO dependencies on any other layer

---

## CQRS

- Separate command and query logic
- Do not mix read/write models
- CommandService: @Transactional, modifies state
- QueryService: @Transactional(readOnly = true), read-only

---

## Clean Architecture

- Domain must be independent
- No framework code in domain
- Repository interfaces (ports) defined in domain
- Repository implementations (adapters) in infrastructure

---

## Anti-Patterns (MUST NOT)

- Business logic in controller
- Business logic in repository
- Business logic in infrastructure
- Returning entity directly from API
- Using repository in controller
- `Impl` suffix (use `Adapter` instead)

---

## Code Consistency

- Follow consistent naming and structure
- Do not mix multiple patterns in same module
- All services follow the same internal structure (domain/application/infrastructure/presentation)

---

# Enforcement

- Any violation of layer boundaries is not allowed
- Code must be easily testable
- Code must be maintainable and readable
