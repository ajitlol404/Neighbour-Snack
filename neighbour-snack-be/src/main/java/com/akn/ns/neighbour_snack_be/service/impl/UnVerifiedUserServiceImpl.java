package com.akn.ns.neighbour_snack_be.service.impl;

import com.akn.ns.neighbour_snack_be.dto.UnVerifiedUserDto;
import com.akn.ns.neighbour_snack_be.dto.UnVerifiedUserDto.UnVerifiedUserPublicResponseDto;
import com.akn.ns.neighbour_snack_be.dto.UnVerifiedUserDto.UnVerifiedUserRequestDto;
import com.akn.ns.neighbour_snack_be.entity.UnVerifiedUser;
import com.akn.ns.neighbour_snack_be.exception.SmtpException;
import com.akn.ns.neighbour_snack_be.exception.UnVerifiedUserException;
import com.akn.ns.neighbour_snack_be.repository.UnVerifiedUserRepository;
import com.akn.ns.neighbour_snack_be.service.SmtpService;
import com.akn.ns.neighbour_snack_be.service.UnVerifiedUserService;
import com.akn.ns.neighbour_snack_be.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.akn.ns.neighbour_snack_be.utility.AppConstant.*;
import static com.akn.ns.neighbour_snack_be.utility.EmailTemplate.ACCOUNT_CREATED_SUCCESS_TEMPLATE;
import static com.akn.ns.neighbour_snack_be.utility.EmailTemplate.UNVERIFIED_USER_REGISTRATION_TEMPLATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnVerifiedUserServiceImpl implements UnVerifiedUserService {

    private final UserService userService;
    private final UnVerifiedUserRepository unVerifiedUserRepository;
    private final SmtpService smtpService;

    @Override
    public UnVerifiedUserPublicResponseDto registerUnVerifiedUser(UnVerifiedUserRequestDto unVerifiedUserRequestDTO) {

        if (unVerifiedUserRepository.existsByEmail(unVerifiedUserRequestDTO.email())) {
            throw new UnVerifiedUserException("The email you have chosen is currently pending verification. Please check your email for the verification link or choose a different email.");
        }

        if (userService.userExistsByEmail(unVerifiedUserRequestDTO.email())) {
            throw new UnVerifiedUserException("The email you have entered is already associated with a verified account. Please use a different email.");
        }

        UnVerifiedUser unVerifiedUser = unVerifiedUserRepository.save(unVerifiedUserRequestDTO.toEntity());

        // Send verification email
        String link = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/unverified-users/" + unVerifiedUser.getId() + "/verify")
                .build()
                .toUriString();

        try {

            smtpService.sendEmail(
                    unVerifiedUserRequestDTO.email(),
                    UNVERIFIED_USER_REGISTRATION_TEMPLATE,
                    UNVERIFIED_USER_REGISTRATION_TEMPLATE.formatBody(
                            StringUtils.capitalize(unVerifiedUserRequestDTO.name()),
                            BRAND_NAME,
                            link,
                            VERIFICATION_EXPIRATION_MINUTES,
                            SUPPORT_TEAM
                    )
            );

        } catch (Exception e) {
            log.error("Email sending failed for unverified user: {}", unVerifiedUserRequestDTO.email(), e);
            throw new SmtpException("Failed to send verification email. Please try again later.");
        }

        return UnVerifiedUserPublicResponseDto.fromEntity(unVerifiedUser);
    }

    @Override
    public UnVerifiedUserPublicResponseDto verifyLink(UUID uuid) {
        try {
            UnVerifiedUser unVerifiedUser = unVerifiedUserRepository.findUnVerifiedUserByUuid(uuid);
            if (isExpired(unVerifiedUser.getCreatedAt())) {
                unVerifiedUserRepository.delete(unVerifiedUser);
                throw new UnVerifiedUserException("Verification link has expired");
            }
            return UnVerifiedUserPublicResponseDto.fromEntity(unVerifiedUser);
        } catch (NoSuchElementException nsee) {
            throw new UnVerifiedUserException("Verification link has expired");
        }
    }

    @Override
    public void verifyAndSaveUser(UUID uuid, UnVerifiedUserDto.UnVerifiedUserPasswordRequestDto unVerifiedUserPasswordDTO) {
        try {

            UnVerifiedUser unVerifiedUser = unVerifiedUserRepository.findUnVerifiedUserByUuid(uuid);
            if (isExpired(unVerifiedUser.getCreatedAt())) {
                unVerifiedUserRepository.delete(unVerifiedUser);
                throw new UnVerifiedUserException("Verification link has expired");
            }

            userService.createCustomer(
                    unVerifiedUser.getName(),
                    unVerifiedUser.getEmail(),
                    unVerifiedUserPasswordDTO.password(),
                    unVerifiedUser.getPhoneNumber()
            );

            unVerifiedUserRepository.delete(unVerifiedUser);

            smtpService.sendEmail(
                    unVerifiedUser.getEmail(),
                    ACCOUNT_CREATED_SUCCESS_TEMPLATE,
                    ACCOUNT_CREATED_SUCCESS_TEMPLATE.formatBody(
                            StringUtils.capitalize(unVerifiedUser.getName()),
                            BRAND_NAME,
                            unVerifiedUser.getEmail(),
                            SUPPORT_TEAM
                    )
            );

        } catch (NoSuchElementException nsee) {
            log.warn("Verification failed: UnVerifiedUser not found for UUID={}", uuid);
            throw new UnVerifiedUserException("Verification link has expired");
        }
    }

    @Override
    public void cleanUpExpiredUnverifiedUsers() {
        ZonedDateTime expiryTime = ZonedDateTime.now().minusMinutes(VERIFICATION_EXPIRATION_MINUTES);
        int deletedCount = unVerifiedUserRepository.deleteAllExpired(expiryTime);
        log.info("Expired unverified users cleanup completed. Deleted: {}", deletedCount);
    }

    private boolean isExpired(ZonedDateTime createdAt) {
        return createdAt
                .plusMinutes(VERIFICATION_EXPIRATION_MINUTES)
                .isBefore(ZonedDateTime.now());
    }

}
