# Coding Standards & Patterns

## Naming Conventions

### Package Naming
- Module: `com.tempo.core.{module}`
- Layers: `application`, `domain`, `infrastructure`, `interfaces`

### Class Naming

| Type | Convention | Example |
| ---- | ---------- | ------- |
| Entity | PascalCase | `User`, `Order` |
| Service | `{Name}ReadService`, `{Name}WriteService` | `AuthReadService` |
| Mapper | `{Module}Mapper` | `AuthMapper` |
| Request DTO | `{Action}{Entity}Request` | `AddUserRequest` |
| Response DTO | `{Entity}Response` | `UserResponse` |
| Business Rule | `{Description}Rule` | `UsernameFormatRule` |
| Domain Event | `{Entity}{Action}Event` | `TenantCreatedEvent` |

### Enum Naming (snake_case values)

```java
public enum Status {
    draft,
    pending,
    confirmed,
    cancelled
}
```

---

## CQRS Service Pattern

### ReadService
```java
@Service
@RequiredArgsConstructor
public class AuthReadService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return authMapper.toUserResponseList(userRepository.findAllWithRoles());
    }
}
```

### WriteService
```java
@Service
@RequiredArgsConstructor
public class AuthWriteService {
    @Transactional
    public void createUser(AddUserRequest request) {
        User user = User.create(tenantId, request.getUsername(), ...);
        userRepository.save(user);
    }
}
```

---

## DTO Pattern

### Request DTO (Lombok class + validation)
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    @NotBlank private String username;
    @NotBlank private String password;
}
```

### Response DTO (Java Record)
```java
public record UserResponse(UUID id, String username, boolean active, Set<String> roleNames) {}
```

---

## MapStruct Mapper
```java
@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "rolesToNameSet")
    UserResponse toUserResponse(User user);
    
    @Named("rolesToNameSet")
    default Set<String> rolesToNameSet(Set<Role> roles) {
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}
```

---

## Entity Hierarchy

| Base Class | Purpose |
| ---------- | ------- |
| `BaseEntity<ID>` | JPA auditing, optimistic locking |
| `BaseDomainEntity<ID>` | Adds `checkRule()` |
| `AggregateRoot<ID>` | Domain event support |
| `TenantAggregateRoot<ID>` | **Most common** - multi-tenant aggregate |

---

## Entity Factory Pattern
```java
public static User create(UUID tenantId, String username, String passwordHash) {
    User user = new User();
    user.checkRule(new TenantIdMustNotBeEmptyRule(tenantId));
    user.checkRule(new UsernameFormatRule(username));
    user.setTenantId(tenantId);
    user.username = username;
    return user;
}
```

---

## Business Rules Pattern
```java
public class UsernameFormatRule implements BusinessRule {
    boolean isBroken();     // Check if violated
    String getMessage();    // Error message
    String getCode();       // Error code for i18n
}
```

---

## Domain Events
```java
public void confirm() {
    checkRule(new OrderMustBePendingRule(this.status));
    this.status = Status.confirmed;
    registerEvent(new OrderConfirmedEvent(this.id));
}
```
