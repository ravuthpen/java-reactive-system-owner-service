package com.piseth.java.school.ownerservice.verification;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SecureOtpGenerator implements OtpGenerator{

    //call SecureRandom for best secure
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String generateNumericOtp(int length) {
        //created builder form StringBuilder
        StringBuilder builder = new StringBuilder(length);

        for(int i = 0; i < length; i++){
            builder.append(SECURE_RANDOM.nextInt(10));
        }
        return builder.toString(); //6 digit
    }
}
