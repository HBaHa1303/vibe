---
paths:
  - "./src/**/infrastructure/**"
---

# Infrastructure Layer Rules

## General

- Technical implementations using Spring, JPA
- Implements repository ports from domain layer

---

## Package Structure

```
infrastructure/
└── persistence/
    ├── entity/<Aggregate>JpaEntity.java
    ├── repository/<Aggregate>JpaRepository.java
    ├── repository/<Aggregate>RepositoryAdapter.java
    └── mapper/<Aggregate>PersistenceMapper.java
```

---

## JpaEntity

- `@Entity`, `@Table`, Lombok `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor`
- Must NOT contain business logic, must NOT be exposed to upper layers

---

## JpaRepository

- Extends `JpaRepository<JpaEntity, UUID>`
- Custom query methods for existence checks

---

## RepositoryAdapter

- `@Repository`, implements domain repository port
- Injected: JpaRepository + PersistenceMapper
- Converts Spring `Page` → domain `PageResult`
- Naming: `<Aggregate>RepositoryAdapter` (NOT `Impl`)

---

## PersistenceMapper

- MapStruct `@Mapper(componentModel = "spring")`
- `toDomain(JpaEntity)` → Domain — use `default` method calling `reconstitute()`
- `toEntity(Domain)` → JPA — auto-generated via `@Builder`
- `@Named` default methods for Value Object conversions (e.g., `fromUsername`)
- `@Mapping(target = "isActive", source = "active")` for boolean mismatch
- Uses `ValueObject.reconstitute()` (NOT `of()`) for DB → Domain mapping

---

## Must

- Implement repository ports from domain
- Map between persistence and domain models via MapStruct
- Use `reconstitute()` for Value Objects from DB
- Use `PageResult` from common (not Spring Page in port interface)

## Must NOT

- Contain business logic
- Leak JPA entities to upper layers
- Use `@Component` on mapper (MapStruct generates it)
