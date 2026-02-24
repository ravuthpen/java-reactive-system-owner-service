package com.piseth.java.school.ownerservice.normalizer;
import org.springframework.stereotype.Component;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OwnerRegisterRequestNormalizer {

    private final EmailNormalizer emailNormalizer;
    private final PhoneNormalizer phoneNormalizer;

    public OwnerRegisterRequest normalize(OwnerRegisterRequest request) {

        request.setEmail(emailNormalizer.normalize(request.getEmail()));
        request.setPhone(phoneNormalizer.normalize(request.getPhone()));

        return request;
    }

    //@TODO don't mutate parameter (create new object)
}