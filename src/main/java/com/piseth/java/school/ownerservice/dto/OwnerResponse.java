package com.piseth.java.school.ownerservice.dto;

import com.piseth.java.school.ownerservice.domain.enums.OwnerStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public class OwnerResponse {
    UUID id;
    String phone;
    OwnerStatus status;
    Instant createdAt;
    Instant updatedAt;
}

// Builder Pattern
