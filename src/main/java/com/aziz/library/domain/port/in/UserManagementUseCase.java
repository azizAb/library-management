package com.aziz.library.domain.port.in;

import java.util.List;

import com.aziz.library.domain.model.Role;
import com.aziz.library.domain.model.User;

public interface UserManagementUseCase {
    User createUser(User user, Long currentUserId);
    User updateUser(Long id, User user, Long currentUserId);
    void deleteUser(Long id, Long currentUserId);
    User getUserById(Long id, Long currentUserId);
    List<User> getAllUsers(Long currentUserId);
    User updateUserRole(Long id, Role role, Long currentUserId);
}
