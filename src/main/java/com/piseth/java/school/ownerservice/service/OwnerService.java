package com.piseth.java.school.ownerservice.service;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OwnerService {

    Mono<OwnerResponse> register(OwnerRegisterRequest request);
    Mono<OwnerResponse> findById(UUID ownerId);
}
