# Naming Conventions

## General

- All names must be in English
- Use meaningful, domain-driven names
- Avoid abbreviations unless widely accepted (e.g. ID, URL)

---

## Classes

- Use PascalCase
- Must reflect responsibility clearly

Examples:
- User
- Order
- CreateOrderCommandService
- UserQueryService
- PaymentClient
- ConfigProvider

---

## Suffix-based Roles (MANDATORY)

### CommandService

- Responsibility:
    - Handle write operations (state changes)
    - Execute command use cases

- Must:
    - Modify state
    - Call domain entities and repositories

- Must NOT:
    - Contain business logic
    - Return domain entities

- Naming:
    - Verb-based

Examples:
- CreateOrderCommandService
- CancelOrderCommandService

---

### QueryService

- Responsibility:
    - Handle all read operations for a specific aggregate or domain

- Structure:
    - One QueryService per aggregate
    - Contains multiple query methods

- Must:
    - NOT modify state
    - NOT contain business logic
    - NOT call CommandService

- Must return:
    - DTO or projection only

- Naming:
    - <AggregateName>QueryService

Examples:
- UserQueryService
- OrderQueryService

---

### Repository (Port Interface)

- Responsibility:
    - Persistence abstraction (domain interface / port)

- Must:
    - Only handle data access abstraction
    - Return domain entities

- Must NOT:
    - Contain business logic

- Naming:
    - <AggregateName>Repository

Examples:
- UserRepository
- OrderRepository

---

### Adapter (Repository Implementation)

- Responsibility:
    - Implement repository port from domain layer
    - Bridge between domain and persistence technology

- Must:
    - Implement domain repository interface
    - Map between persistence and domain models

- Must NOT:
    - Contain business logic
    - Leak JPA entities to upper layers

- Naming:
    - <AggregateName>RepositoryAdapter

Examples:
- UserRepositoryAdapter
- OrderRepositoryAdapter

---

### JpaRepository (Spring Data Interface)

- Responsibility:
    - Spring Data JPA repository interface

- Naming:
    - <AggregateName>JpaRepository

Examples:
- UserJpaRepository
- OrderJpaRepository

---

### JpaEntity (Persistence Model)

- Responsibility:
    - ORM entity for database mapping

- Naming:
    - <AggregateName>JpaEntity

Examples:
- UserJpaEntity
- OrderJpaEntity

---

### PersistenceMapper

- Responsibility:
    - Convert between JPA entity and domain entity

- Naming:
    - <AggregateName>PersistenceMapper

Examples:
- UserPersistenceMapper
- OrderPersistenceMapper

---

### Client

- Responsibility:
    - External system communication (HTTP, gRPC, etc.)

- Must:
    - Handle request/response only

- Must NOT:
    - Contain business logic

Examples:
- PaymentClient
- InventoryClient

---

### Provider

- Responsibility:
    - Provide system-level or contextual data

- Must:
    - Be stateless
    - Be side-effect free

- Must NOT:
    - Contain business logic

Examples:
- TimeProvider
- CurrentUserProvider
- ConfigProvider

---

### DtoMapper (Presentation)

- Responsibility:
    - Convert between presentation DTOs and application commands/responses

- Naming:
    - <AggregateName>DtoMapper

Examples:
- UserDtoMapper
- OrderDtoMapper

---

### ExceptionHandler

- Responsibility:
    - Handle exceptions for REST API responses

- Naming:
    - <AggregateName>ExceptionHandler

Examples:
- UserExceptionHandler
- OrderExceptionHandler

---

### Policy / Strategy / Calculator

- Responsibility:
    - Encapsulate business rules or algorithms

Use:
- Policy -> business rule
- Strategy -> interchangeable behavior
- Calculator -> computation

Examples:
- DiscountPolicy
- PricingStrategy
- TaxCalculator

---

## Methods

- Use camelCase
- Must be verb-based

Examples:
- createOrder
- cancelOrder
- calculateTotal

---

## Variables

- Use camelCase
- Must be descriptive and unambiguous

Examples:
- totalPrice
- orderItems

---

## Boolean

- Must start with:
    - is / has / can / should

Examples:
- isActive
- hasItems

---

## DTO / Command / Query

- Must be explicit and descriptive
- Use Java records

Examples:
- CreateUserCommand
- UpdateUserCommand
- UserResponse
- UserPageResponse
- CreateUserRequest
- UserDetailResponse

---

## Value Objects

- Must be immutable (final class, final fields)
- Two factory methods:
    - `of(String value)` - validates input, throws on invalid
    - `reconstitute(String value)` - no validation, for infrastructure mapping
- Private constructor

Examples:
- Email.of("user@example.com")
- Email.reconstitute("user@example.com")

---

## Aggregate Roots

- Two factory methods:
    - `create(...)` - for new entities, validates inputs, sets defaults
    - `reconstitute(...)` - for rebuilding from persistence, no validation
- Behavior methods instead of setters

Examples:
- User.create(username, email, ...)
- User.reconstitute(id, username, email, ...)
- user.updateProfile(...)
- user.deactivate()

---

## CQRS Enforcement

- CommandService must NOT be used for read operations
- QueryService must NOT modify state
- CommandService must NOT return domain entities
- QueryService must return DTO or projection only

---

## QueryService Strategy

- Use QueryService for simple queries within a single aggregate
- Use dedicated QueryHandler for complex or cross-aggregate queries

---

## QueryService Enforcement

- Must NOT exceed reasonable size (avoid god class)
- Must group related queries only (same aggregate)
- Must NOT contain complex business logic
- Must NOT reuse domain entities directly in response
- Must NOT access CommandService

---

## Anti-Patterns (MUST NOT)

- No generic names:
    - Helper, Util, Manager, CommonService

- No `Impl` suffix for repository implementations (use `Adapter` instead)

- No Vietnamese in code

- No ambiguous naming

- Do NOT mix responsibilities in class naming
