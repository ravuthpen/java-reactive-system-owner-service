package com.piseth.java.school.ownerservice.domain;

import com.piseth.java.school.ownerservice.domain.enums.VerificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("owner_verifications")
public class Verification {

    @Id
    private UUID id;
    private UUID ownerId;
    private VerificationType type;
    private String target;
    private String otpHash;
    private String otpSalt;
    private Instant expiresAt;
    private Integer attemptCount;
    private Integer maxAttempts;
    private boolean verified;
    private Instant verifiedAt;
    private Instant createdAt;
    private Instant updatedAt;

}
