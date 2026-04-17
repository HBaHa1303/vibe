---
paths:
  - "./src/**/presentation/**"
---

# Presentation Layer Rules

## General

- Handles HTTP / API layer, must be thin (no business logic)
- Calls application layer only, uses DtoMapper for all conversions
- DTOs separate from domain models

---

## CQRS Controller Separation

- `<Aggregate>CommandController` — write (POST, PUT, DELETE)
- `<Aggregate>QueryController` — read (GET)

### Endpoints

| Action | Method | Path | Return |
|--------|--------|------|--------|
| Create | POST | `/api/<resources>` | 201 + Location header |
| Update | PUT | `/api/<resources>/{id}` | 204 No Content |
| Delete | DELETE | `/api/<resources>/{id}` | 204 No Content |
| Get by ID | GET | `/api/<resources>/{id}` | 200 + DetailResponse |
| List | GET | `/api/<resources>?page=&size=` | 200 + PageResponse |

---

## Request DTOs

- Java records, raw values only (String, UUID)
- Naming: `<Verb><Aggregate>Request`

---

## Response DTOs

- Java records
- `<Aggregate>DetailResponse` — full fields for single item
- `<Aggregate>SummaryResponse` — subset for list items
- `PageResponse<T>` (generic from common) wraps list with `PagingInfo`
- Page JSON: `{ data: [...], paging: { page, size, totalElements, totalPages } }`

---

## DtoMapper

- MapStruct `@Mapper(componentModel = "spring")`
- Maps application Result → presentation Response
- Methods: `toResponse(Result)`, `toSummaryResponse(Result)`, `toPageResponse(PageResult)`
- No FQN needed — application uses `Result`, presentation uses `Response`

---

## ExceptionHandler

- `@RestControllerAdvice`, returns `ProblemDetail` (RFC 7807)
- `DomainException` → 400, `NoSuchElementException` → 404
- Fields: `type`, `title`, `status`, `detail`, `instance`

---

## Must NOT

- Access domain or infrastructure directly
- Contain business rules
- Return domain entities
- Mix command and query operations in one controller
