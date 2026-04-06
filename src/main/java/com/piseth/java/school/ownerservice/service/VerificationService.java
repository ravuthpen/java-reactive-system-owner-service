package com.piseth.java.school.ownerservice.service;

import com.piseth.java.school.ownerservice.domain.enums.VerificationType;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationService {

    Mono<Void> sendOtp(UUID ownerId, VerificationType type);
    Mono<Void> verifyOtp(UUID ownerId, VerificationType type, String otp);
}
