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
        //@TODO don't mutate (do not modify) parameter (create new object)
        /**
         * old object
         request.setEmail(emailNormalizer.normalize(request.getEmail()));
         request.setPhone(phoneNormalizer.normalize(request.getPhone()));
         return request;
         */
        //created new object
        OwnerRegisterRequest newRequest = new OwnerRegisterRequest();
        newRequest.setEmail(emailNormalizer.normalize(request.getEmail()));
        newRequest.setPhone(phoneNormalizer.normalize(request.getPhone()));
        return newRequest;


    }
}