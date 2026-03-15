package com.piseth.java.school.ownerservice.repository;

import com.piseth.java.school.ownerservice.domain.Owner;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OwnerRepository extends ReactiveCrudRepository<Owner, UUID> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByPhone(String phone);
}
