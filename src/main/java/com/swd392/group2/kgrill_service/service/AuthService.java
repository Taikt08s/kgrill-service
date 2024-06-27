package com.swd392.group2.kgrill_service.service;

import com.nimbusds.jose.JOSEException;
import com.swd392.group2.kgrill_service.dto.*;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    void register(RegistrationRequest request) throws MessagingException, UnsupportedEncodingException;

    AuthenticationResponse authenticate(AuthenticationRequest request) throws JOSEException;

    void activateAccount(String token, HttpServletResponse response) throws MessagingException, UnsupportedEncodingException;

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    AuthenticationResponse findOrCreateUser(GoogleAuthenticationRequest request);
}
