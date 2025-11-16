package com.aziz.library.infrastructure.adapter.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aziz.library.domain.model.LoginAttempt;
import com.aziz.library.domain.port.out.LoginAttemptRepositoryPort;
import com.aziz.library.infrastructure.adapter.persistence.entity.LoginAttemptEntity;
import com.aziz.library.infrastructure.adapter.persistence.repository.LoginAttemptRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginAttemptRepositoryAdapter implements LoginAttemptRepositoryPort{

    private final LoginAttemptRepository loginAttemptRepository;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LoginAttempt save(LoginAttempt loginAttempt) {
        LoginAttemptEntity entity = toEntity(loginAttempt);
        LoginAttemptEntity saved = loginAttemptRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public List<LoginAttempt> findByIdentifierAndAttemptTimeAfter(String identifier, LocalDateTime after) {
        return loginAttemptRepository.findByIdentifierAndAttemptTimeAfter(identifier, after).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public int countFailedAttempts(String identifier, LocalDateTime after) {
        return loginAttemptRepository.countFailedAttempts(identifier, after);
    }
    
    @Override
    public void deleteOldAttempts(LocalDateTime before) {
        loginAttemptRepository.deleteByAttemptTimeBefore(before);
    }
    
    private LoginAttempt toDomain(LoginAttemptEntity entity) {
        return LoginAttempt.builder()
                .id(entity.getId())
                .identifier(entity.getIdentifier())
                .ipAddress(entity.getIpAddress())
                .successful(entity.isSuccessful())
                .attemptTime(entity.getAttemptTime())
                .build();
    }
    
    private LoginAttemptEntity toEntity(LoginAttempt domain) {
        return LoginAttemptEntity.builder()
                .id(domain.getId())
                .identifier(domain.getIdentifier())
                .ipAddress(domain.getIpAddress())
                .successful(domain.isSuccessful())
                .attemptTime(domain.getAttemptTime())
                .build();
    }

}
