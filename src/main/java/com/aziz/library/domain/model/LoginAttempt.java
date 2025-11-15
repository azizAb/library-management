package com.aziz.library.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttempt {
    private Long id;
    private String identifier;
    private String ipAddress;
    private boolean successful;
    private LocalDateTime attemptTime;
}
