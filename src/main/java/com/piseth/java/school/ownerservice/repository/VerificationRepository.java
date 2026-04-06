package com.piseth.java.school.ownerservice.repository;

import com.piseth.java.school.ownerservice.domain.Verification;
import com.piseth.java.school.ownerservice.domain.enums.VerificationType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationRepository extends ReactiveCrudRepository<Verification, UUID> {

    // not best practice
    Mono<Verification> findFirstByOwnerIdAndTypeAndVerifiedFalseOrderByCreatedAtDesc(UUID ownerId, VerificationType type);
    Flux<Verification> findByOwnerIdAndTypeAndVerifiedFalse(UUID ownerId, VerificationType type);


    /**
    //Get latest unverified verification by owner and type
    Mono<Verification> findTopByOwnerIdAndTypeAndVerifiedIsFalseOrderByCreatedAtDesc(
            UUID ownerId,
            VerificationType type
    );

    //Get all active (unverified) verifications
    Flux<Verification> findAllByOwnerIdAndTypeAndVerifiedIsFalse(
            UUID ownerId,
            VerificationType type
    );
     */
}
