package com.piseth.java.school.ownerservice.notification;

import com.piseth.java.school.ownerservice.domain.enums.VerificationType;

public interface NotificationSender {
    /**
     * Send OTP notification to a target (email or phone)
     */
    void send(String target, VerificationType type, String otp);
}
