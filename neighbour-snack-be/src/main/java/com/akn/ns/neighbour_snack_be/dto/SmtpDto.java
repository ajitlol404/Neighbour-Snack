package com.akn.ns.neighbour_snack_be.dto;

import com.akn.ns.neighbour_snack_be.entity.Smtp;
import com.akn.ns.neighbour_snack_be.utility.AppUtil;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.ZonedDateTime;

import static com.akn.ns.neighbour_snack_be.utility.AppConstant.MAX_STRING_DEFAULT_SIZE;
import static com.akn.ns.neighbour_snack_be.utility.AppConstant.MIN_STRING_DEFAULT_SIZE;
import static com.akn.ns.neighbour_snack_be.utility.AppUtil.generateCode;

public class SmtpDto {

    @Data
    public static class SmtpFilterRequest {

        private String keyword; // search by name, host, username etc.
        private int page = 0; // page number, default 0
        private int size = 10; // page size, default 10
        private String sortBy = "updatedAt"; // default sort column
        private String sortDir = "desc"; // default sort direction

    }

    public record SmtpRequestDto(
            @NotBlank(message = "{smtp.name.required}")
            @Size(min = MIN_STRING_DEFAULT_SIZE, max = 100, message = "{smtp.name.size}")
            @Pattern(
                    regexp = "^[a-zA-Z0-9()\\-]+$",
                    message = "{smtp.name.pattern}"
            )
            String name,

            @NotBlank(message = "{smtp.host.required}")
            @Size(min = MIN_STRING_DEFAULT_SIZE, max = 255, message = "{smtp.host.size}")
            String host,

            @NotNull(message = "{smtp.port.required}")
            @Min(value = 1, message = "{smtp.port.min}")
            @Max(value = 65535, message = "{smtp.port.max}")
            Integer port,

            @NotBlank(message = "{smtp.username.required}")
            @Size(min = MIN_STRING_DEFAULT_SIZE, max = MAX_STRING_DEFAULT_SIZE, message = "{smtp.username.size}")
            String username,

            @NotBlank(message = "{smtp.password.required}")
            @Size(min = MIN_STRING_DEFAULT_SIZE, max = MAX_STRING_DEFAULT_SIZE, message = "{smtp.password.size}")
            String password,

            boolean isSsl,

            boolean isActive
    ) {
        public Smtp toEntity() {
            return Smtp.builder()
                    .code(generateCode("SMT"))
                    .name(name.strip().toLowerCase())
                    .host(host)
                    .port(port)
                    .username(username)
                    .password(AppUtil.encodeBase64(password))
                    .isSsl(isSsl)
                    .isActive(isActive)
                    .build();
        }

        public Smtp updateSmtp(Smtp smtp) {
            smtp.setName(name.strip().toLowerCase());
            smtp.setHost(host);
            smtp.setPort(port);
            smtp.setUsername(username);
            smtp.setPassword(AppUtil.encodeBase64(password));
            smtp.setSsl(isSsl);
            smtp.setActive(isActive);
            return smtp;
        }

    }

    public record SmtpResponseDto(
            String code,
            String name,
            String host,
            int port,
            String username,
            boolean isSsl,
            boolean isActive,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt
    ) {
        public static SmtpResponseDto fromEntity(Smtp smtp) {
            return new SmtpResponseDto(
                    smtp.getCode(),
                    smtp.getName(),
                    smtp.getHost(),
                    smtp.getPort(),
                    smtp.getUsername(),
                    smtp.isSsl(),
                    smtp.isActive(),
                    smtp.getCreatedAt(),
                    smtp.getUpdatedAt()
            );
        }
    }

}