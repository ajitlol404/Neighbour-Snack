package com.akn.ns.neighbour_snack_be.dto;

import com.akn.ns.neighbour_snack_be.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;

import static com.akn.ns.neighbour_snack_be.utility.AppConstant.MAX_STRING_DEFAULT_SIZE;
import static com.akn.ns.neighbour_snack_be.utility.AppConstant.MIN_STRING_DEFAULT_SIZE;
import static java.lang.Boolean.TRUE;

public class AuthDto {

    public record SignInRequestDto(
            @NotBlank(message = "{auth.signin.email.required}")
            @Email(message = "{auth.signin.email.invalid}")
            String email,

            @NotBlank(message = "{auth.signin.password.required}")
            String password
    ) {
    }

    public record SignUpRequestDto(
            @NotBlank @Size(min = MIN_STRING_DEFAULT_SIZE, max = 50) String name,
            @NotBlank @Email @Size(min = 5, max = MAX_STRING_DEFAULT_SIZE) String email,
            @NotBlank @Size(min = 8, max = 32) String password,
            @NotBlank @Size(min = 10, max = 10) String phoneNumber,
            String role
    ) {
        public User toEntity(String encodedPassword, User.Role assignedRole) {
            return User.builder()
                    .name(name.strip().toLowerCase())
                    .email(email.strip().toLowerCase())
                    .password(encodedPassword)
                    .phoneNumber(phoneNumber)
                    .role(assignedRole)
                    .isActive(TRUE)
                    .build();
        }
    }

    public record SignUpResponseDto(
            String code,
            String name,
            String email,
            String phoneNumber,
            boolean isActive,
            String role,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt
    ) {
        public static SignUpResponseDto fromEntity(User user) {
            return new SignUpResponseDto(
                    user.getCode(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.isActive(),
                    user.getRole().name(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }
    }

}
