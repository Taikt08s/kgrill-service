package com.swd392.group2.kgrill_service.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.ParseException;
import java.util.Map;

public interface JwtService {
    String extractUsername(String jwtToken);

    String generateToken(Map<String,Object> claims, UserDetails userDetails);

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateEncryptedToken(Map<String, Object> claims, UserDetails userDetails) throws JOSEException;

    String decryptJwt(String encryptedToken) throws ParseException, JOSEException;
}

