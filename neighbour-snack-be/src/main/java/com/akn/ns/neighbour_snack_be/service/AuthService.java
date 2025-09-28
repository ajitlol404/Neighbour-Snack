package com.akn.ns.neighbour_snack_be.service;

import com.akn.ns.neighbour_snack_be.dto.AuthDto;
import com.akn.ns.neighbour_snack_be.dto.AuthDto.SignUpResponseDto;
import com.akn.ns.neighbour_snack_be.dto.JwtResponseDto;

public interface AuthService {

    JwtResponseDto signin(AuthDto.SignInRequestDto signInRequestDto);

    SignUpResponseDto signup(AuthDto.SignUpRequestDto signUpRequestDto);

}
