package com.piseth.java.school.ownerservice.domain;

import com.piseth.java.school.ownerservice.domain.enums.OwnerStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("owners")
public class Owner {

    @Id
    private UUID id;    // keep null if DB generates it
    private String email;
    private String phone;
    private OwnerStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
