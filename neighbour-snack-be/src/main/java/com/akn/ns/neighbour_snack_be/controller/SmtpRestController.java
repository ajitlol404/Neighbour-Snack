package com.akn.ns.neighbour_snack_be.controller;

import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpRequestDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpResponseDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpToggleRequestDto;
import com.akn.ns.neighbour_snack_be.service.SmtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.akn.ns.neighbour_snack_be.utility.AppConstant.BASE_API_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_API_PATH + "/smtp")
@RequiredArgsConstructor
public class SmtpRestController {

    private final SmtpService smtpService;

    @PostMapping
    public ResponseEntity<SmtpResponseDto> create(@Valid @RequestBody SmtpRequestDto request) {
        return ResponseEntity.status(CREATED).body(smtpService.createSmtp(request));
    }

    @PutMapping("/{code}")
    public ResponseEntity<SmtpResponseDto> update(
            @PathVariable String code,
            @Valid @RequestBody SmtpRequestDto request
    ) {
        return ResponseEntity.ok(smtpService.updateSmtp(code, request));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        smtpService.deleteSmtp(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SmtpResponseDto>> getAll() {
        return ResponseEntity.ok(smtpService.getAllSmtps());
    }

    @GetMapping("/{code}")
    public ResponseEntity<SmtpResponseDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(smtpService.getSmtpByCode(code));
    }

    @PatchMapping("/{code}/toggle-status")
    public ResponseEntity<SmtpResponseDto> toggleStatus(
            @PathVariable String code,
            @Valid @RequestBody SmtpToggleRequestDto smtpToggleRequestDto
    ) {
        return ResponseEntity.ok(smtpService.toggleSmtpStatus(code, smtpToggleRequestDto));
    }


}
