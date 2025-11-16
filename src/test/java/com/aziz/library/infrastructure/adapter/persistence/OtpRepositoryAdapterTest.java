package com.aziz.library.infrastructure.adapter.persistence;

import com.aziz.library.domain.model.OtpToken;
import com.aziz.library.infrastructure.adapter.persistence.entity.OtpTokenEntity;
import com.aziz.library.infrastructure.adapter.persistence.repository.OtpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OtpRepositoryAdapterTest {

    private OtpRepository otpRepository;
    private OtpRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        otpRepository = mock(OtpRepository.class);
        adapter = new OtpRepositoryAdapter(otpRepository);
    }

    @Test
    void save_shouldConvertAndSaveEntityAndReturnDomain() {
        OtpToken otpToken = sampleOtpToken();
        OtpTokenEntity savedEntity = sampleOtpTokenEntity();

        when(otpRepository.save(any(OtpTokenEntity.class))).thenReturn(savedEntity);

        OtpToken result = adapter.save(otpToken);

        ArgumentCaptor<OtpTokenEntity> captor = ArgumentCaptor.forClass(OtpTokenEntity.class);
        verify(otpRepository).save(captor.capture());
        OtpTokenEntity passedEntity = captor.getValue();

        assertThat(passedEntity.getUserId()).isEqualTo(otpToken.getUserId());
        assertThat(result.getId()).isEqualTo(savedEntity.getId());
        assertThat(result.getToken()).isEqualTo(savedEntity.getToken());
    }

    @Test
    void findByUserIdAndToken_shouldReturnMappedDomainIfPresent() {
        Long userId = 1L;
        String token = "abc123";
        OtpTokenEntity entity = sampleOtpTokenEntity();

        when(otpRepository.findByUserIdAndToken(userId, token)).thenReturn(Optional.of(entity));

        Optional<OtpToken> result = adapter.findByUserIdAndToken(userId, token);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(entity.getId());
        assertThat(result.get().getToken()).isEqualTo(entity.getToken());
    }

    @Test
    void findByUserIdAndToken_shouldReturnEmptyIfNotFound() {
        when(otpRepository.findByUserIdAndToken(anyLong(), anyString())).thenReturn(Optional.empty());

        Optional<OtpToken> result = adapter.findByUserIdAndToken(1L, "notfound");

        assertThat(result).isEmpty();
    }

    @Test
    void findLatestByUserId_shouldReturnMappedDomainIfPresent() {
        Long userId = 2L;
        OtpTokenEntity entity = sampleOtpTokenEntity();

        when(otpRepository.findLatestByUserId(userId)).thenReturn(Optional.of(entity));

        Optional<OtpToken> result = adapter.findLatestByUserId(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(entity.getUserId());
    }

    @Test
    void findLatestByUserId_shouldReturnEmptyIfNotFound() {
        when(otpRepository.findLatestByUserId(anyLong())).thenReturn(Optional.empty());

        Optional<OtpToken> result = adapter.findLatestByUserId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteByUserId_shouldDelegateToRepository() {
        Long userId = 5L;

        adapter.deleteByUserId(userId);

        verify(otpRepository).deleteByUserId(userId);
    }

    // Helper methods for sample data
    private OtpToken sampleOtpToken() {
        return OtpToken.builder()
                .id(1L)
                .userId(2L)
                .token("token123")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private OtpTokenEntity sampleOtpTokenEntity() {
        return OtpTokenEntity.builder()
                .id(1L)
                .userId(2L)
                .token("token123")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}