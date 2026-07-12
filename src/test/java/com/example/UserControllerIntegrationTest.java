package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void helloEndpoint_returns200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Spring Boot!"));
    }

    @Test
    void createUser_validRequest_returns201() throws Exception {
        UserRequest request = new UserRequest("Test User", "test@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createUser_blankName_returns400() throws Exception {
        UserRequest request = new UserRequest("", "test@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").value("Name is required"));
    }

    @Test
    void createUser_invalidEmail_returns400() throws Exception {
        UserRequest request = new UserRequest("Test User", "not-an-email");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.email").value("Email must be valid"));
    }

    @Test
    void createUser_duplicateEmail_returns409() throws Exception {
        UserRequest request = new UserRequest("User One", "dup@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        UserRequest duplicate = new UserRequest("User Two", "dup@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void getById_existingUser_returns200() throws Exception {
        User user = new User();
        user.setName("Existing User");
        user.setEmail("existing@example.com");
        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Existing User"))
                .andExpect(jsonPath("$.email").value("existing@example.com"));
    }

    @Test
    void getById_nonExistingUser_returns404() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found with id: 9999"));
    }

    @Test
    void updateUser_existingUser_returns200() throws Exception {
        User user = new User();
        user.setName("Original");
        user.setEmail("original@example.com");
        user = userRepository.save(user);

        UserRequest updateRequest = new UserRequest("Updated", "updated@example.com");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void updateUser_nonExistingUser_returns404() throws Exception {
        UserRequest request = new UserRequest("Name", "email@example.com");

        mockMvc.perform(put("/api/users/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_existingUser_returns204() throws Exception {
        User user = new User();
        user.setName("ToDelete");
        user.setEmail("delete@example.com");
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_nonExistingUser_returns404() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_returnsPaginatedResults() throws Exception {
        User user1 = new User();
        user1.setName("User A");
        user1.setEmail("a@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User B");
        user2.setEmail("b@example.com");
        userRepository.save(user2);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void search_validName_returnsResults() throws Exception {
        User user = new User();
        user.setName("Searchable User");
        user.setEmail("search@example.com");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/search")
                        .param("name", "Searchable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Searchable User"));
    }

    @Test
    void search_blankName_returns400() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("name", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Search parameter 'name' must not be blank"));
    }

    @Test
    void getByEmail_existingEmail_returns200() throws Exception {
        User user = new User();
        user.setName("Email User");
        user.setEmail("findme@example.com");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/email/{email}", "findme@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Email User"));
    }

    @Test
    void getByEmail_nonExistingEmail_returns404() throws Exception {
        mockMvc.perform(get("/api/users/email/{email}", "notfound@example.com"))
                .andExpect(status().isNotFound());
    }
}
