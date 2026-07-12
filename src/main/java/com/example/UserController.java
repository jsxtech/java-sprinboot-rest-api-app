package com.example;

import jakarta.validation.Valid;
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
    public Page<UserResponse> getAll(@PageableDefault(size = 10) Pageable pageable) {
        log.info("Fetching users with pagination: {}", pageable);
        return userService.getAll(pageable).map(UserResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public UserResponse getById(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        return UserResponse.from(userService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new user")
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        log.info("Creating user");
        return UserResponse.from(userService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        log.info("Updating user with id: {}", id);
        return UserResponse.from(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user")
    public void delete(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.delete(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name")
    public List<UserResponse> search(@RequestParam String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Search parameter 'name' must not be blank");
        }
        log.info("Searching users by name");
        return userService.searchByName(name).stream()
                .map(UserResponse::from)
                .toList();
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public UserResponse getByEmail(@PathVariable String email) {
        log.info("Fetching user by email");
        return UserResponse.from(userService.findByEmail(email));
    }
}
