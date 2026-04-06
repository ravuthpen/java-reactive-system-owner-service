package com.piseth.java.school.ownerservice.service.impl;


import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.domain.enums.VerificationType;
import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;
import com.piseth.java.school.ownerservice.exception.OwnerNotFoundException;
import com.piseth.java.school.ownerservice.factory.OwnerFactory;
import com.piseth.java.school.ownerservice.mapper.OwnerMapper;
import com.piseth.java.school.ownerservice.normalizer.OwnerRegisterRequestNormalizer;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;
import com.piseth.java.school.ownerservice.service.OwnerService;
import com.piseth.java.school.ownerservice.service.VerificationService;
import com.piseth.java.school.ownerservice.validation.OwnerRegistrationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService{
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final OwnerFactory ownerFactory;
    private final OwnerRegistrationValidator registrationValidator;
    private final OwnerRegisterRequestNormalizer normalizer;
    /**
     * call more private final VerificationService
     */
    private final VerificationService verificationService;


    @Override
    public Mono<OwnerResponse> register(OwnerRegisterRequest request) {
        log.info("Owner registration requested");

        // call OwnerRegisterRequest = OwnerRegisterRequestNormalizer normalizer
        OwnerRegisterRequest normalized = normalizer.normalize(request);
        // mapping => toOwnerDraft insert OwnerRegisterRequest
        Owner draft = ownerMapper.toOwnerDraft(normalized);
        //call OwnerFactory insert
        Owner pending = ownerFactory.newPendingOwner(draft);
        return registrationValidator.validate(normalized)
                .then(Mono.defer(() -> ownerRepository.save(pending)))
                // Send OTPs available
                .flatMap(savedOwner -> sendOtpForAvailableChannels(savedOwner).thenReturn(savedOwner))
                .doOnSuccess(saved -> log.info("Owner registered successfully. ownerId={}", saved.getId()))
                .map(ownerMapper::toResponse);
    }

    @Override
    public Mono<OwnerResponse> findById(UUID ownerId) {
        return ownerRepository.findById(ownerId)
                .switchIfEmpty(Mono.error( new OwnerNotFoundException(ownerId)))
                .doOnSuccess(owner -> log.info("Owner found successfully. ownerId={}", owner.getId()))
                .map(ownerMapper::toResponse);
    }

    /**
     * Send OTP Available Channels (Email and Phone).
     * This method checks which contact are available for the owner
     * - if email exists -> send OTP via EMAIL
     * - if phone exists -> send OTP via PHONE
     * Both operations are executed sequentially:
     * email -> then phone
     * @param owner the owner containing email and phone
     * @return Mono<Void> indicating async completion of all OTP sends
     */
    private Mono<Void> sendOtpForAvailableChannels(Owner owner){

        // Default empty Monos(no-op if channel not available)
        Mono<Void> emailOtpMono = Mono.empty();
        Mono<Void> phoneOtpMono = Mono.empty();

        // Send OTP via EMAIL if available
        if(StringUtils.hasText(owner.getEmail())){
            emailOtpMono = verificationService.sendOtp(owner.getId(), VerificationType.EMAIL);
        }

        // Send OTP vai PHONE if available
        if(StringUtils.hasText(owner.getPhone())){
            phoneOtpMono = verificationService.sendOtp(owner.getId(), VerificationType.PHONE);
        }
        // Execute sequentially: email first, then phone
        return emailOtpMono.then(phoneOtpMono);
    }


}