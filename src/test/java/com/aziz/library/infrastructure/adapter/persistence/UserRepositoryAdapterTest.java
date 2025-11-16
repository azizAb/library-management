package com.aziz.library.infrastructure.adapter.persistence;

import com.aziz.library.domain.model.Role;
import com.aziz.library.domain.model.User;
import com.aziz.library.infrastructure.adapter.persistence.entity.UserEntity;
import com.aziz.library.infrastructure.adapter.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserRepositoryAdapterTest {

    private UserRepository userRepository;
    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        adapter = new UserRepositoryAdapter(userRepository);
    }

    private UserEntity sampleEntity() {
        return UserEntity.builder()
                .id(1L)
                .fullname("John Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("secret")
                .role(Role.VIEWER)
                .enabled(true)
                .accountLocked(false)
                .lockUntil(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User sampleUser() {
        return User.builder()
                .id(1L)
                .fullname("John Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("secret")
                .role(Role.VIEWER)
                .enabled(true)
                .accountLocked(false)
                .lockUntil(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_shouldConvertAndSaveUser() {
        User user = sampleUser();
        UserEntity entity = sampleEntity();

        when(userRepository.save(any(UserEntity.class))).thenReturn(entity);

        User result = adapter.save(user);

        assertThat(result.getId()).isEqualTo(entity.getId());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void findById_shouldReturnUserIfExists() {
        UserEntity entity = sampleEntity();
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(entity.getUsername());
    }

    @Test
    void findById_shouldReturnEmptyIfNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = adapter.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_shouldReturnUserIfExists() {
        UserEntity entity = sampleEntity();
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByUsername("johndoe");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(entity.getEmail());
    }

    @Test
    void findByEmail_shouldReturnUserIfExists() {
        UserEntity entity = sampleEntity();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByEmail("john@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(entity.getUsername());
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUserIfExists() {
        UserEntity entity = sampleEntity();
        when(userRepository.findByUsernameOrEmail("johndoe", "johndoe")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByUsernameOrEmail("johndoe");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(entity.getUsername());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        UserEntity entity1 = sampleEntity();
        UserEntity entity2 = UserEntity.builder()
                .id(2L)
                .fullname(entity1.getFullname())
                .username("janedoe")
                .email("jane@example.com")
                .password(entity1.getPassword())
                .role(entity1.getRole())
                .enabled(entity1.isEnabled())
                .accountLocked(entity1.isAccountLocked())
                .lockUntil(entity1.getLockUntil())
                .createdAt(entity1.getCreatedAt())
                .updatedAt(entity1.getUpdatedAt())
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        List<User> users = adapter.findAll();

        assertThat(users).hasSize(2);
        assertThat(users.get(1).getUsername()).isEqualTo("janedoe");
    }

    @Test
    void deleteById_shouldCallRepository() {
        adapter.deleteById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void existsByUsername_shouldReturnTrueIfExists() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        boolean exists = adapter.existsByUsername("johndoe");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnTrueIfExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        boolean exists = adapter.existsByEmail("john@example.com");

        assertThat(exists).isTrue();
    }
}