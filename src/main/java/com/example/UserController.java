package com.example;

import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    public Page<User> getAll(@PageableDefault(size = 10) Pageable pageable) {
        log.info("Fetching users with pagination: {}", pageable);
        return userService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Cacheable(value = "user", key = "#id")
    @Operation(summary = "Get user by ID")
    public User getById(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        return userService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "user", allEntries = true)
    @Operation(summary = "Create new user")
    public User create(@Valid @RequestBody User user) {
        log.info("Creating user");
        return userService.create(user);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "user", key = "#id")
    @Operation(summary = "Update user")
    public User update(@PathVariable Long id, @Valid @RequestBody User user) {
        log.info("Updating user with id: {}", id);
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "user", key = "#id")
    @Operation(summary = "Delete user")
    public void delete(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.delete(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name")
    public List<User> search(@RequestParam String name) {
        log.info("Searching users by name");
        return userService.searchByName(name);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public User getByEmail(@PathVariable String email) {
        log.info("Fetching user by email");
        return userService.findByEmail(email);
    }
}
