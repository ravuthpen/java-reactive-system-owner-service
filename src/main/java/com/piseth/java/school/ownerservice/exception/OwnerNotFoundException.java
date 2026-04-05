package com.piseth.java.school.ownerservice.exception;

import java.io.Serial;
import java.util.UUID;

public class OwnerNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public OwnerNotFoundException(UUID ownerId){
        super("Owner Not Found with id: " + ownerId);
    }
}
