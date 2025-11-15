package com.aziz.library.infrastructure.adapter.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "login_attempt", indexes = {
    @Index(name = "idx_identifier", columnList = "identifier"),
    @Index(name = "idx_attempt_time", columnList = "attempt_time")
})
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String identifier;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(nullable = false)
    private boolean successful;
    
    @Column(name = "attempt_time", nullable = false)
    private LocalDateTime attemptTime;
}
