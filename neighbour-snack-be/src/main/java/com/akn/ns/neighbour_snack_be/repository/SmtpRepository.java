package com.akn.ns.neighbour_snack_be.repository;

import com.akn.ns.neighbour_snack_be.entity.Smtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public interface SmtpRepository extends JpaRepository<Smtp, UUID>, JpaSpecificationExecutor<Smtp> {

    boolean existsByName(String name);

    default Smtp findSmtpByUuid(UUID uuid) {
        return findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("Smtp with [UUID: " + uuid + "] not found"));
    }

    @Modifying
    @Transactional
    @Query("UPDATE Smtp s SET s.isActive = false WHERE s.isActive = true")
    void deactivateAll();

    long countByIsActiveTrue();

    Optional<Smtp> findByIsActiveTrue();

    default Smtp findActiveSmtp() {
        return findByIsActiveTrue()
                .orElseThrow(() -> new NoSuchElementException("No active SMTP configuration found"));
    }

    Optional<Smtp> findByCode(String code);

    default Smtp findSmtpByCode(String code) {
        return findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Smtp with [CODE: " + code + "] not found"));
    }

}
