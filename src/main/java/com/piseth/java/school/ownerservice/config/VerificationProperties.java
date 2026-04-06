package com.piseth.java.school.ownerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.verification")
public class VerificationProperties {
    /**
     * OTP length send 6 digit
     */
    private int otpLength = 6;
    /**
     * OTP TTl seconds (ex. 300 = 5 minute)
     */
    private long otpTtlSeconds = 300;
    /**
     * Maximum verification attempts
     */
    private int maxAttempts = 5;
    /**
     * Secret pepper (store in ENV in productions)
     */
    private String otpPepper = "CHANGE-ME-IN-PROD";
}
