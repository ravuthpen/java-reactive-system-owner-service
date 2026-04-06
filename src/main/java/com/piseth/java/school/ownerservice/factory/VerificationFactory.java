package com.piseth.java.school.ownerservice.factory;

import com.piseth.java.school.ownerservice.config.VerificationProperties;
import com.piseth.java.school.ownerservice.domain.Verification;
import com.piseth.java.school.ownerservice.domain.enums.VerificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VerificationFactory {
    /**
     * inject times
     */
    private final Clock clock;
    private final VerificationProperties verificationProperties;

    /**
     * create field or column saved to database :
     * ownerId
     * type
     * target
     * otpHash
     * otpSalt
     */
    public Verification newVerification(
            UUID ownerId,
            VerificationType type,
            String target,
            String otpHash,
            String otpSalt
    ){
        /**
         * created new instant form builder
         * expiresAt, maxAttempts get from configuration
         */
        Instant now = Instant.now(clock);
        return Verification.builder()
                .ownerId(ownerId)
                .type(type)
                .target(target)
                .otpHash(otpHash)
                .otpSalt(otpSalt)
                .expiresAt(now.plusSeconds(verificationProperties.getOtpTtlSeconds()))
                .attemptCount(0)
                .maxAttempts(verificationProperties.getMaxAttempts())
                .verified(false)
                .verifiedAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
