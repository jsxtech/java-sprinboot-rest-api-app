package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john@example.com");
    }

    @Test
    void getAll_returnsPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(sampleUser));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void getById_existingId_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        User result = userService.getById(1L);

        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User not found with id: 99");
    }

    @Test
    void create_validRequest_returnsCreatedUser() {
        UserRequest request = new UserRequest("Jane Smith", "jane@example.com");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        User result = userService.create(request);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Jane Smith");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void update_existingId_returnsUpdatedUser() {
        UserRequest request = new UserRequest("Updated Name", "updated@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void update_nonExistingId_throwsException() {
        UserRequest request = new UserRequest("Name", "email@example.com");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User not found with id: 99");
    }

    @Test
    void delete_existingId_deletesUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User not found with id: 99");
    }

    @Test
    void searchByName_returnsMatchingUsers() {
        when(userRepository.findTop50ByNameContainingIgnoreCase("john"))
                .thenReturn(List.of(sampleUser));

        List<User> results = userService.searchByName("john");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void findByEmail_existingEmail_returnsUser() {
        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(sampleUser));

        User result = userService.findByEmail("john@example.com");

        assertThat(result.getName()).isEqualTo("John Doe");
    }

    @Test
    void findByEmail_nonExistingEmail_throwsException() {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("unknown@example.com"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User not found with email: unknown@example.com");
    }
}
