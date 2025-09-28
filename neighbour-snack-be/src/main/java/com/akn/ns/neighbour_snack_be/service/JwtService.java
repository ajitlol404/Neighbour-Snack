package com.akn.ns.neighbour_snack_be.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.function.Function;

public interface JwtService {

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(UserDetails userDetails);

    List<String> extractRoles(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

}