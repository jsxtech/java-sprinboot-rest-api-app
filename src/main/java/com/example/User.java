package com.example;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        // Entities are equal only when both have a persisted id and the ids match.
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        // Constant hash is intentional: id is assigned on persist, so a value-based
        // hash would change across the entity lifecycle and break hash-based collections.
        return getClass().hashCode();
    }
}
