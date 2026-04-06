package com.piseth.java.school.ownerservice.verification;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class OtpSaltGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generate(){
        byte[] bytes = new byte[16]; //array
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        //Base64 :
    }
}
