# Naming Conventions

## General

- All names must be in English
- Use meaningful, domain-driven names
- Avoid abbreviations unless widely accepted (e.g. ID, URL)
- Classes: PascalCase, reflect responsibility clearly
- Methods: camelCase, verb-based
- Variables: camelCase, descriptive and unambiguous
- Booleans: start with is / has / can / should

---

## DTO / Command / Result / Response

- Use Java records
- Application layer uses `Result` suffix, presentation uses `Response` suffix

| Layer | Single item | Page |
|-------|------------|------|
| Application | `UserResult` | `PageResult<UserResult>` (generic from common) |
| Presentation | `UserDetailResponse` | `PageResponse<UserSummaryResponse>` (generic from common) |
| Presentation Request | `CreateUserRequest`, `UpdateUserRequest` | — |
| Application Command | `CreateUserCommand`, `UpdateUserCommand` | — |

---

## Value Objects

- Immutable (final class, final fields, `@Getter`)
- Two factory methods: `of(value)` validates, `reconstitute(value)` no validation
- Private constructor

---

## Aggregate Roots

- `create(...)` — for new entities, validates inputs, sets defaults
- `reconstitute(...)` — for rebuilding from persistence, no validation
- Behavior methods instead of setters

---

## CQRS Enforcement

- CommandService: write only, must NOT return domain entities
- QueryService: read only, must NOT modify state, returns Result only
- QueryService: one per aggregate, must NOT exceed reasonable size
- Use dedicated QueryHandler for complex or cross-aggregate queries

---

## Anti-Patterns (MUST NOT)

- Generic names: Helper, Util, Manager, CommonService
- `Impl` suffix for repository implementations (use `Adapter`)
- Vietnamese in code
- Ambiguous naming
- Mix responsibilities in class naming
