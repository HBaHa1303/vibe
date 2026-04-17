---
paths:
  - "./src/**/application/**"
---

# Application Layer Rules

## General

- Contains UseCases (Command and Query handlers)
- Strict CQRS separation

---

## Command Side

- Depends on domain layer (repository ports, entities, value objects)
- Orchestrates domain logic, manages transactions (`@Transactional`)
- Must NOT import from infrastructure
- Returns void or simple type (e.g., UUID)

### CommandService

- `@Service`, `@Transactional`
- Constructs Value Objects, checks preconditions, calls domain factory methods
- Naming: `<Verb><Aggregate>CommandService`

### Command Objects

- Java records, raw values only (String, UUID), NOT Value Objects
- Naming: `<Verb><Aggregate>Command`

---

## Query Side

- Depends on infrastructure directly (JPA repositories)
- Bypasses domain layer entirely (read-optimized)
- Must NOT import from domain layer

### QueryService

- `@Service`, `@Transactional(readOnly = true)`
- Uses JPA repository + QueryMapper
- Must NOT contain private mapping helpers
- Returns Result types only
- Naming: `<Aggregate>QueryService`

### QueryMapper

- MapStruct `@Mapper(componentModel = "spring")`, lives in same package as QueryService
- `toResult(JpaEntity)` → `<Aggregate>Result`
- `toPageResult(Page<JpaEntity>)` → `PageResult<AggregateResult>`
- Use `@Mapping(target = "isActive", source = "active")` for boolean mismatch

---

## Result Types

- Java records, flat primitive/standard types
- Returned by QueryService to Presentation layer
- Naming: `<Aggregate>Result`
- Pagination: use generic `PageResult<T>` from common

---

## Must

- Command side: construct VOs, check preconditions, use domain factory methods
- Query side: use JPA repository + QueryMapper directly
- Return Results only (never domain entities)
- Keep Command and Query completely separate

## Must NOT

- Contain business logic
- Command side: import from infrastructure
- Query side: import from domain layer
- Mix command and query concerns
- Return domain entities or JPA entities
