package com.piseth.java.school.ownerservice.notification;

import com.piseth.java.school.ownerservice.domain.enums.VerificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * console implementation of NotificationSender
 * this class is used for development and testing purpose only.
 * Instant of sending read notification (email/phone).
 * it logs OTP to the console.
 */

@Component
@Slf4j
public class ConsoleNotificationSender implements NotificationSender{
    /**
     * Simulates sending as OTP notification by logging it.
     * @param target the destination (email address or phone number)
     * @param type the verification type (EMAIL or PHONE)
     * @param otp the one-time pass code (6 digit)
     */
    @Override
    public void send(String target, VerificationType type, String otp) {
        log.info("Console OTP send. type={}, target={}, otp={}", type, target, otp);
    }
}
