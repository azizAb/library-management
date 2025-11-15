package com.aziz.library.application.mapper;

import org.mapstruct.Mapper;

import com.aziz.library.application.dto.response.AuditLogResponse;
import com.aziz.library.domain.model.AuditLog;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    AuditLogResponse toResponse(AuditLog auditLog);

}
