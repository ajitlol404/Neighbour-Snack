package com.akn.ns.neighbour_snack_be.controller;

import com.akn.ns.neighbour_snack_be.dto.AuthDto.SignInRequestDto;
import com.akn.ns.neighbour_snack_be.dto.AuthDto.SignUpRequestDto;
import com.akn.ns.neighbour_snack_be.dto.AuthDto.SignUpResponseDto;
import com.akn.ns.neighbour_snack_be.dto.JwtResponseDto;
import com.akn.ns.neighbour_snack_be.service.AuthService;
import com.akn.ns.neighbour_snack_be.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.akn.ns.neighbour_snack_be.utility.AppConstant.BASE_API_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_API_PATH + "/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponseDto> signin(@Valid @RequestBody SignInRequestDto loginRequestDto) {
        return ResponseEntity.status(CREATED).body(authService.signin(loginRequestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signup(@Valid @RequestBody SignUpRequestDto signupRequestDto) {
        return ResponseEntity.status(CREATED).body(authService.signup(signupRequestDto));
    }

}

