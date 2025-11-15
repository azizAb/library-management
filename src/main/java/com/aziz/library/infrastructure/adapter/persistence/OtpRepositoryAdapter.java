package com.aziz.library.infrastructure.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.aziz.library.domain.model.OtpToken;
import com.aziz.library.domain.port.out.OtpRepositoryPort;
import com.aziz.library.infrastructure.adapter.persistence.entity.OtpTokenEntity;
import com.aziz.library.infrastructure.adapter.persistence.repository.OtpRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OtpRepositoryAdapter implements OtpRepositoryPort{

    private final OtpRepository otpRepository;
    
    @Override
    public OtpToken save(OtpToken otpToken) {
        OtpTokenEntity entity = toEntity(otpToken);
        OtpTokenEntity saved = otpRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<OtpToken> findByUserIdAndToken(Long userId, String token) {
        return otpRepository.findByUserIdAndToken(userId, token)
                .map(this::toDomain);
    }
    
    @Override
    public Optional<OtpToken> findLatestByUserId(Long userId) {
        return otpRepository.findLatestByUserId(userId)
                .map(this::toDomain);
    }
    
    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        otpRepository.deleteByUserId(userId);
    }
    
    private OtpToken toDomain(OtpTokenEntity entity) {
        return OtpToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .used(entity.isUsed())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    private OtpTokenEntity toEntity(OtpToken domain) {
        return OtpTokenEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .token(domain.getToken())
                .expiresAt(domain.getExpiresAt())
                .used(domain.isUsed())
                .createdAt(domain.getCreatedAt())
                .build();
    }

}
