package com.piseth.java.school.ownerservice.service.impl;

import com.piseth.java.school.ownerservice.config.VerificationProperties;
import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.domain.Verification;
import com.piseth.java.school.ownerservice.domain.enums.OwnerStatus;
import com.piseth.java.school.ownerservice.domain.enums.VerificationType;
import com.piseth.java.school.ownerservice.exception.BadRequestException;
import com.piseth.java.school.ownerservice.exception.OwnerNotFoundException;
import com.piseth.java.school.ownerservice.factory.VerificationFactory;
import com.piseth.java.school.ownerservice.notification.NotificationSender;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;
import com.piseth.java.school.ownerservice.repository.VerificationRepository;
import com.piseth.java.school.ownerservice.service.VerificationService;
import com.piseth.java.school.ownerservice.verification.OtpGenerator;
import com.piseth.java.school.ownerservice.verification.OtpHasher;
import com.piseth.java.school.ownerservice.verification.OtpSaltGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * implement of VerificationService
 * this service handle lifecycle:
 * - Generate OTP
 * - Send OTP via notification channel/target
 * - Verify OTP
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService {
    private final OwnerRepository ownerRepository;
    private final VerificationRepository verificationRepository;
    private final VerificationFactory verificationFactory;
    private final OtpGenerator otpGenerator;
    private final OtpHasher otpHasher;
    private final OtpSaltGenerator otpSaltGenerator;
    private final NotificationSender notificationSender;
    private final VerificationProperties verificationProperties;
    private final Clock clock;

    /**
     * Generate nad send OTP to the owner based on verification type.
     * Flow:
     * 1. Find owner by id
     * 2. Validate owner exists
     * 3. Resole target (email/phone)
     * 4. Checked is already verified
     * 5. Generate OTP (numeric)
     * 6. Generate salt + hash (security)
     * 7. Invalidate previous unverified OTPs
     * 8. Save new verification recode
     * 9. Send OTP via NotificstionSender
     * @param ownerId the owner ID
     * @param type verification type (email/phone)
     * @return Mono<Void> async completion signal
     * @throws OwnerNotFoundException if owner does not exist
     */

    @Override
    public Mono<Void> sendOtp(UUID ownerId, VerificationType type) {
        //before send OTP we generate OTP first

        // Step 1: Find Owner
        return ownerRepository.findById(ownerId)
                // Step 2: Throw error if owner not found
                .switchIfEmpty(Mono.error(new OwnerNotFoundException(ownerId)))
                // Step 3: Process OTP flow
                .flatMap(owner ->{
                    // Resolve target (email or phone)
                    String target = resoleTarget(owner, type);
                    //Validate not already verified
                    validateAlreadyVerified(owner, type);

                    // Step 4: Generate OTP
                    String otp = otpGenerator.generateNumericOtp(verificationProperties.getOtpLength());
                    // Step 5: Generate salt + hash (security)
                    String salt = otpSaltGenerator.generate();
                    String hash = otpHasher.hash(otp, salt, verificationProperties.getOtpPepper());

                    // Step 6: Create verification entity
                    Verification verification = verificationFactory.newVerification(
                            owner.getId(),
                            type,
                            target,
                            hash,
                            salt
                    );

                    // Step 7: Invalidate old OTPs (mark as verified)
                    return verificationRepository.findByOwnerIdAndTypeAndVerifiedFalse(ownerId, type)
                            .flatMap(existing -> {
                                existing.setVerified(true);
                                existing.setVerifiedAt(Instant.now(clock));
                                existing.setUpdatedAt(Instant.now(clock));
                                return verificationRepository.save(existing);
                            })
                            // Step 8: saved new OTP
                            .then(verificationRepository.save(verification))
                            // Step 9: Send OTP (side - effect)
                            .doOnSuccess(saved ->{
                                notificationSender.send(target, type, otp);
                                log.info("OTP generated and sent. owner id={}, type={}", ownerId, type);
                            })
                            // Step 10: Return Mono<Void>
                            .then();
                });
    }

    @Override
    public Mono<Void> verifyOtp(UUID ownerId, VerificationType type, String otp) {
        return ownerRepository.findById(ownerId)
                .switchIfEmpty(Mono.error(new OwnerNotFoundException(ownerId)))
                .flatMap(owner -> verificationRepository
                        .findFirstByOwnerIdAndTypeAndVerifiedFalseOrderByCreatedAtDesc(ownerId, type)
                        .switchIfEmpty(Mono.error(new BadRequestException("Verification code not found. Please request a new OTP.")))
                        .flatMap(verification -> validateAndConsumeOtp(owner, verification, otp, type))
                );
    }

    private Mono<Void> validateAndConsumeOtp(
            Owner owner,
            Verification verification,
            String otp,
            VerificationType type
    ){
        Instant now = Instant.now(clock);
        if(verification.isVerified()){
            return Mono.error(new BadRequestException("OTP has already been used."));
        }

        if (verification.getExpiresAt().isBefore(now)) {
            return Mono.error(new BadRequestException("OTP has expired."));
        }

        if(verification.getAttemptCount() >= verification.getMaxAttempts()){
            return Mono.error(new BadRequestException("OTP maximum attempts exceeded. Please request a new OTP."));
        }

        boolean matches = otpHasher.matches(
                otp,
                verification.getOtpSalt(),
                verificationProperties.getOtpPepper(),
                verification.getOtpHash()
        );

        if(!matches){
            verification.setAttemptCount(verification.getAttemptCount()+1);
            verification.setUpdatedAt(now);

            return verificationRepository.save(verification)
                    .then(Mono.error(new BadRequestException("Invalid OTP.")));
        }

        verification.setVerified(true);
        verification.setVerifiedAt(now);
        verification.setUpdatedAt(now);

        applyVerificationToOwner(owner, type, now);

        return verificationRepository.save(verification)
                .then(ownerRepository.save(owner))
                .doOnSuccess(saved -> log.info("OTP verified successfully. ownerId={}, type={}", owner.getId(), type))
                .then();
    }

    private void applyVerificationToOwner(Owner owner, VerificationType type, Instant now){
        if (VerificationType.EMAIL.equals(type)){
            owner.setEmailVerifiedAt(now);
        }
        if (VerificationType.PHONE.equals(type)){
            owner.setPhoneVerifiedAt(now);
        }
        if(isOwnerFullyVerified(owner)){
            owner.setStatus(OwnerStatus.ACTIVE);
        }
        owner.setUpdatedAt(now);
    }

    private boolean isOwnerFullyVerified(Owner owner){
        boolean emailRequired = StringUtils.hasText(owner.getEmail());
        boolean phoneRequired = StringUtils.hasText(owner.getPhone());

        boolean emailOk = !emailRequired || owner.getEmailVerifiedAt() != null;
        boolean phoneOk = !phoneRequired || owner.getPhoneVerifiedAt() != null;

        return emailOk && phoneOk;
    }

    /**
     * Resole the notification target (email or phone number) based on verification type.
     * This method determines where the OTP should be sent:
     * - if type = EMAIL -> return user's email
     * - if type = PHONE -. return user's phone
     * Typically used before sending OTP via NotificationSender.
     * @param owner the owner domain containing contact information
     * @param type the verification type (EMAIL or PHONE)
     * @return the resolved target (email address or phone number)
     * @throws IllegalArgumentException if the required contact info is missing
     */
    private String resoleTarget(Owner owner, VerificationType type){
        /**
         * validate that owner has a non-empty email
         */
        if(VerificationType.EMAIL.equals(type)){
            if(!StringUtils.hasText(owner.getEmail())){
                throw new BadRequestException("Owner does not have an email");
            }
            /**
             * return email as notification target
             */
            return owner.getEmail();
        }
        /**
         * validate that owner has a non-empty phone
         */
        if(VerificationType.PHONE.equals(type)){
            if(!StringUtils.hasText(owner.getPhone())){
                throw new BadRequestException("Owner does not have a phone");
            }
            /**
             * return phone as notification target
             */
            return owner.getPhone();
        }
        /**
         * validate that owner has non-empty email and phone
         */
        throw new BadRequestException("Unsupported verification type.");
    }

    /**
     * Validate tha the given verification type has not already been verified.
     * This method prevents sending or verifying OTP again if the owner has already completed verification for the given type.
     * - EMAIL -> checked emailVerifiedAt
     * - PHONE -> checked phoneVerifiedAt
     * @param owner the owner domain containing contact information
     * @param type the verification type (EMAIL or PHONE)
     * @throws BadRequestException if the verification is already complete
     */
    private void validateAlreadyVerified(Owner owner, VerificationType type){
        // checked if email already verified
        if(VerificationType.EMAIL.equals(type) && owner.getEmailVerifiedAt() != null){
            throw new BadRequestException("Email already verified.");
        }
        // checked id phone already verified
        if(VerificationType.PHONE.equals(type) && owner.getPhoneVerifiedAt() != null){
            throw new BadRequestException("Phone already verified.");
        }
    }
}
