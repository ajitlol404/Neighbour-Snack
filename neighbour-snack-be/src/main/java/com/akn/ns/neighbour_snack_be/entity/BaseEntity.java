package com.akn.ns.neighbour_snack_be.entity;

import com.akn.ns.neighbour_snack_be.utility.AppUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.akn.ns.neighbour_snack_be.utility.AppConstant.NUMERIC_CHARACTERS;
import static com.akn.ns.neighbour_snack_be.utility.AppConstant.UPPERCASE_CHARACTERS;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    protected String generateReadableId(String prefix) {
        return prefix + "_" + AppUtil.generateRandomString(UPPERCASE_CHARACTERS + NUMERIC_CHARACTERS);
    }

}

