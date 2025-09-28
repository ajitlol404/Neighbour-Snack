package com.akn.ns.neighbour_snack_be.service.impl;

import com.akn.ns.neighbour_snack_be.dto.AuthDto;
import com.akn.ns.neighbour_snack_be.dto.JwtResponseDto;
import com.akn.ns.neighbour_snack_be.entity.User;
import com.akn.ns.neighbour_snack_be.repository.UserRepository;
import com.akn.ns.neighbour_snack_be.security.CustomUserDetails;
import com.akn.ns.neighbour_snack_be.service.AuthService;
import com.akn.ns.neighbour_snack_be.service.JwtService;
import com.akn.ns.neighbour_snack_be.utility.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.akn.ns.neighbour_snack_be.entity.User.Role.ROLE_CUSTOMER;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Override
    public JwtResponseDto signin(AuthDto.SignInRequestDto signInRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequestDto.email(),
                        signInRequestDto.password()
                )
        );

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(customUserDetails);
        return new JwtResponseDto(jwtToken);
    }

    @Override
    public AuthDto.SignUpResponseDto signup(AuthDto.SignUpRequestDto signUpRequestDto) {
        if (userRepository.existsByEmailIgnoreCase(signUpRequestDto.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        if (userRepository.existsByPhoneNumber(signUpRequestDto.phoneNumber())) {
            throw new IllegalArgumentException("Phone number is already in use");
        }

        User.Role role;
        try {
            role = User.Role.valueOf("ROLE_" + signUpRequestDto.role().toUpperCase());
        } catch (Exception e) {
            role = ROLE_CUSTOMER; // default role
        }

        String prefix = getPrefixForRole(role);
        String userCode = AppUtil.generateCode(prefix);

        User user = signUpRequestDto.toEntity(passwordEncoder.encode(signUpRequestDto.password()), role);
        user.setCode(userCode);

        User savedUser = userRepository.save(user);

        return AuthDto.SignUpResponseDto.fromEntity(savedUser);
    }

    private String getPrefixForRole(User.Role role) {
        return switch (role) {
            case ROLE_ADMIN -> "ADM";
            case ROLE_CUSTOMER -> "CUS";
            case ROLE_MANAGER -> "MAN";
            case ROLE_DELIVERY -> "DEL";
        };
    }

}
