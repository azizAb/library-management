package com.aziz.library.infrastructure.adapter.persistence;

import com.aziz.library.domain.model.LoginAttempt;
import com.aziz.library.infrastructure.adapter.persistence.entity.LoginAttemptEntity;
import com.aziz.library.infrastructure.adapter.persistence.repository.LoginAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginAttemptRepositoryAdapterTest {

    private LoginAttemptRepository loginAttemptRepository;
    private LoginAttemptRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        loginAttemptRepository = mock(LoginAttemptRepository.class);
        adapter = new LoginAttemptRepositoryAdapter(loginAttemptRepository);
    }

    @Test
    void save_shouldSaveAndReturnDomainObject() {
        LoginAttempt domain = LoginAttempt.builder()
                .id(1L)
                .identifier("user")
                .ipAddress("127.0.0.1")
                .successful(false)
                .attemptTime(LocalDateTime.now())
                .build();

        LoginAttemptEntity entity = LoginAttemptEntity.builder()
                .id(1L)
                .identifier("user")
                .ipAddress("127.0.0.1")
                .successful(false)
                .attemptTime(domain.getAttemptTime())
                .build();

        when(loginAttemptRepository.save(any(LoginAttemptEntity.class))).thenReturn(entity);

        LoginAttempt result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(domain.getId(), result.getId());
        assertEquals(domain.getIdentifier(), result.getIdentifier());
        assertEquals(domain.getIpAddress(), result.getIpAddress());
        assertEquals(domain.isSuccessful(), result.isSuccessful());
        assertEquals(domain.getAttemptTime(), result.getAttemptTime());

        ArgumentCaptor<LoginAttemptEntity> captor = ArgumentCaptor.forClass(LoginAttemptEntity.class);
        verify(loginAttemptRepository).save(captor.capture());
        LoginAttemptEntity savedEntity = captor.getValue();
        assertEquals(domain.getIdentifier(), savedEntity.getIdentifier());
    }

    @Test
    void findByIdentifierAndAttemptTimeAfter_shouldReturnMappedDomainObjects() {
        String identifier = "user";
        LocalDateTime after = LocalDateTime.now().minusDays(1);

        LoginAttemptEntity entity = LoginAttemptEntity.builder()
                .id(2L)
                .identifier(identifier)
                .ipAddress("192.168.1.1")
                .successful(true)
                .attemptTime(LocalDateTime.now())
                .build();

        when(loginAttemptRepository.findByIdentifierAndAttemptTimeAfter(identifier, after))
                .thenReturn(Collections.singletonList(entity));

        List<LoginAttempt> result = adapter.findByIdentifierAndAttemptTimeAfter(identifier, after);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
        assertEquals(entity.getIdentifier(), result.get(0).getIdentifier());
        assertEquals(entity.getIpAddress(), result.get(0).getIpAddress());
        assertEquals(entity.isSuccessful(), result.get(0).isSuccessful());
        assertEquals(entity.getAttemptTime(), result.get(0).getAttemptTime());
    }

    @Test
    void countFailedAttempts_shouldDelegateToRepository() {
        String identifier = "user";
        LocalDateTime after = LocalDateTime.now().minusHours(2);

        when(loginAttemptRepository.countFailedAttempts(identifier, after)).thenReturn(3);

        int count = adapter.countFailedAttempts(identifier, after);

        assertEquals(3, count);
        verify(loginAttemptRepository).countFailedAttempts(identifier, after);
    }

    @Test
    void deleteOldAttempts_shouldDelegateToRepository() {
        LocalDateTime before = LocalDateTime.now().minusDays(10);

        adapter.deleteOldAttempts(before);

        verify(loginAttemptRepository).deleteByAttemptTimeBefore(before);
    }
}