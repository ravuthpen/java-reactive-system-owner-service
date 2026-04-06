package com.piseth.java.school.ownerservice.verification;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class OtpHasher {
    public String hash(String otp, String salt, String paper){
        String value = otp + ":" + salt + ":" + paper;
        return sha256(value);
    }

    public boolean matches(String rawOtp, String salt, String paper, String storedHash){
        return hash(rawOtp, salt, paper).equals(storedHash);
    }

    private String sha256(String value){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return toHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", ex);
        }
    }
    private String toHex(byte[] bytes){
        StringBuilder builder = new StringBuilder();

        for (byte b : bytes){
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
