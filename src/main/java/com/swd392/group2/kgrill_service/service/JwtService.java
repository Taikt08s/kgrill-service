package com.swd392.group2.kgrill_service.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {
    String extractUsername(String jwtToken);

    String generateToken(Map<String,Object> claims, UserDetails userDetails);

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);
}
