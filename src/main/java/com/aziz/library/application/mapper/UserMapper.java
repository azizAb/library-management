package com.aziz.library.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.aziz.library.application.dto.request.UserRequest;
import com.aziz.library.application.dto.response.UserResponse;
import com.aziz.library.domain.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountLocked", constant = "false")
    @Mapping(target = "lockUntil", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toDomain(UserRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateDomainFromRequest(UserRequest request, @MappingTarget User user);

}
