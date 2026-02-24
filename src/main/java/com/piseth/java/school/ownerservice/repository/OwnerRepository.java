package com.piseth.java.school.ownerservice.repository;

import com.piseth.java.school.ownerservice.domain.Owner;
import lombok.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OwnerRepository extends ReactiveCrudRepository<@NonNull Owner, @NonNull UUID> {
    Mono<@NonNull Boolean> existsByEmail(String email);
    Mono<@NonNull Boolean> existsByPhone(String phone);
}
