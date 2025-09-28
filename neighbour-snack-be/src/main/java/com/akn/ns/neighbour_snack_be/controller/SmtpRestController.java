package com.akn.ns.neighbour_snack_be.controller;

import com.akn.ns.neighbour_snack_be.dto.PaginationResponse;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpFilterRequest;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpRequestDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpResponseDto;
import com.akn.ns.neighbour_snack_be.service.SmtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.akn.ns.neighbour_snack_be.utility.AppConstant.BASE_API_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_API_PATH + "/smtp")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
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
    public ResponseEntity<PaginationResponse<SmtpResponseDto>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        SmtpFilterRequest filterRequest = new SmtpFilterRequest();
        filterRequest.setKeyword(keyword);
        filterRequest.setPage(page);
        filterRequest.setSize(size);
        filterRequest.setSortBy(sortBy);
        filterRequest.setSortDir(sortDir);

        return ResponseEntity.ok(smtpService.getAllSmtps(filterRequest));
    }

    @GetMapping("/{code}")
    public ResponseEntity<SmtpResponseDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(smtpService.getSmtpByCode(code));
    }

    @PatchMapping("/{code}/toggle-status")
    public ResponseEntity<SmtpResponseDto> toggleStatus(@PathVariable String code) {
        return ResponseEntity.ok(smtpService.toggleSmtpStatus(code));
    }

}
