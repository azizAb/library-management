package com.aziz.library.domain.port.out;

import java.util.Optional;

import com.aziz.library.domain.model.OtpToken;

public interface OtpRepositoryPort {
    OtpToken save(OtpToken otpToken);
    Optional<OtpToken> findByUserIdAndToken(Long userId, String token);
    Optional<OtpToken> findLatestByUserId(Long userId);
    void deleteByUserId(Long userId);   
}
