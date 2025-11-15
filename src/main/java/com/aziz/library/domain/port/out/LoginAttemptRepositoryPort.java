package com.aziz.library.domain.port.out;

import java.time.LocalDateTime;
import java.util.List;

import com.aziz.library.domain.model.LoginAttempt;

public interface LoginAttemptRepositoryPort {
    LoginAttempt save(LoginAttempt loginAttempt);
    List<LoginAttempt> findByIdentifierAndAttemptTimeAfter(String identifier, LocalDateTime after);
    int countFailedAttempts(String identifier, LocalDateTime after);
    void deleteOldAttempts(LocalDateTime before);
}
