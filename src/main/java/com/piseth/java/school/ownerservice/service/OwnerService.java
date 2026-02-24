package com.piseth.java.school.ownerservice.service;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface OwnerService {

    Mono<@NonNull OwnerResponse> register(OwnerRegisterRequest request);
}
