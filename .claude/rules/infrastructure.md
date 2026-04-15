---
paths:
  - "./src/**/infrastructure/**"
---

# Infrastructure Layer Rules

## General

- Contains technical implementations
- Can use Spring, JPA, annotations
- Implements repository interfaces (ports) defined in domain

---

## Contains

- JPA Entities (persistence model)
- Spring Data JPA Repositories
- Repository Adapters (implement domain port)
- Persistence Mappers
- External API clients
- Messaging configurations

---

## Package Structure

```
infrastructure/
└── persistence/
    ├── entity/
    │   └── <Aggregate>JpaEntity.java
    ├── repository/
    │   ├── <Aggregate>JpaRepository.java      # Spring Data interface
    │   └── <Aggregate>RepositoryAdapter.java   # Implements domain port
    └── mapper/
        └── <Aggregate>PersistenceMapper.java
```

---

## JPA Entity

- Annotated with `@Entity`, `@Table`
- Use Lombok: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Fields map to database columns
- Naming: `<Aggregate>JpaEntity`
- Must NOT contain business logic
- Must NOT be exposed to upper layers

---

## Spring Data JPA Repository

- Extends `JpaRepository<JpaEntity, UUID>`
- Custom query methods for existence checks
- Naming: `<Aggregate>JpaRepository`

---

## Repository Adapter

- Annotated with `@Repository`
- Implements domain repository interface (port)
- Injected: Spring Data JPA repository + Persistence mapper
- Converts between Spring's `Page` and domain's `PageResult`
- Naming: `<Aggregate>RepositoryAdapter` (NOT `Impl`)

Example:
```java
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

---

## Persistence Mapper

- Annotated with `@Component`
- Two main methods:
    - `toDomain(JpaEntity)` -> Domain entity (uses `reconstitute()` factories)
    - `toEntity(DomainEntity)` -> JPA entity (extracts VO values)
- Batch method: `toDomainList(List<JpaEntity>)`
- Uses `ValueObject.reconstitute()` (NOT `of()`) when mapping from DB
- Uses `valueObject.getValue()` when mapping to JPA entity
- Naming: `<Aggregate>PersistenceMapper`

---

## Must

- Implement repository interfaces defined in domain
- Map between persistence model and domain model
- Use `reconstitute()` for Value Objects when loading from DB
- Use `PageResult` from common module (not Spring Page in port interface)

---

## Must NOT

- Contain business logic
- Leak JPA entities to domain or application layer
- Call domain behavior methods (domain handles its own state)
- Use `ValueObject.of()` for reconstitution (use `reconstitute()` instead)

---

# Enforcement

- No business rules in repository implementations
- No returning JPA entities to upper layers
- No `Impl` suffix (use `Adapter`)
