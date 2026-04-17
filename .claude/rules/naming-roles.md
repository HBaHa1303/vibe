---
name: naming-roles
description: Suffix-based class naming roles
---

# Suffix-based Roles (MANDATORY)

## CommandService

- Handle write operations, execute command use cases
- Must modify state, call domain entities and repositories
- Must NOT contain business logic, return domain entities
- Naming: verb-based — `CreateOrderCommandService`, `CancelOrderCommandService`

---

## QueryService

- Handle all read operations for a specific aggregate
- One per aggregate, multiple query methods
- Must NOT modify state, contain business logic, call CommandService
- Must return Result or projection only
- Naming: `<Aggregate>QueryService` — `UserQueryService`

---

## QueryMapper

- MapStruct `@Mapper(componentModel = "spring")` in application layer (same package as QueryService)
- Maps JPA entities to application Result types (flat fields, no Value Objects)
- Pagination: `default` method converts Spring `Page` to `PageResult`
- Naming: `<Aggregate>QueryMapper` — `UserQueryMapper`

---

## Repository (Port Interface)

- Persistence abstraction in domain layer
- Return domain entities or `Optional`
- Use `PageResult<T>` from common for pagination
- Naming: `<Aggregate>Repository` — `UserRepository`

---

## Adapter (Repository Implementation)

- Implement domain repository port
- Bridge between domain and persistence
- Must NOT leak JPA entities to upper layers
- Naming: `<Aggregate>RepositoryAdapter` — `UserRepositoryAdapter`

---

## JpaRepository

- Spring Data JPA interface
- Extends `JpaRepository<JpaEntity, UUID>`
- Naming: `<Aggregate>JpaRepository` — `UserJpaRepository`

---

## JpaEntity

- ORM entity for database mapping
- Use Lombok: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Naming: `<Aggregate>JpaEntity` — `UserJpaEntity`

---

## PersistenceMapper

- MapStruct `@Mapper(componentModel = "spring")` in infrastructure
- `toDomain(JpaEntity)` → Domain (use `default` method with `reconstitute()`)
- `toEntity(Domain)` → JPA (auto-generated via `@Builder`)
- Uses `@Named` default methods for Value Object conversions
- Naming: `<Aggregate>PersistenceMapper` — `UserPersistenceMapper`

---

## DtoMapper (Presentation)

- MapStruct `@Mapper(componentModel = "spring")`
- Converts between presentation DTOs and application results/commands
- Methods: `toResponse(Result)`, `toSummaryResponse(Result)`, `toPageResponse(PageResult)`
- Naming: `<Aggregate>DtoMapper` — `UserDtoMapper`

---

## ExceptionHandler

- `@RestControllerAdvice`, returns `ProblemDetail` (RFC 7807)
- Catches `DomainException` → 400, `NoSuchElementException` → 404
- Error fields: `type`, `title`, `status`, `detail`
- Naming: `<Aggregate>ExceptionHandler` — `UserExceptionHandler`

---

## Client

- External system communication (HTTP, gRPC)
- Handle request/response only, no business logic
- Examples: `PaymentClient`, `InventoryClient`

---

## Provider

- Provide system-level or contextual data
- Stateless, side-effect free, no business logic
- Examples: `TimeProvider`, `CurrentUserProvider`

---

## Policy / Strategy / Calculator

- Policy → business rule
- Strategy → interchangeable behavior
- Calculator → computation
- Examples: `DiscountPolicy`, `PricingStrategy`, `TaxCalculator`
