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
public class User {
    private Long id;
    private String fullname;
    private String username;
    private String email;
    private String password;
    private Role role;
    private boolean enabled;
    private boolean accountLocked;
    private LocalDateTime lockUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
