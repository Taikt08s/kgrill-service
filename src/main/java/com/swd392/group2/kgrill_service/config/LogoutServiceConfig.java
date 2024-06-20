package com.swd392.group2.kgrill_service.config;

import com.swd392.group2.kgrill_model.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LogoutServiceConfig implements LogoutHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogoutServiceConfig.class);

    private final  TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("text/plain");
            try {
                response.getWriter().write("No JWT token found in the request header");
            } catch (IOException e) {
                logger.error("Error writing unauthorized response", e);
            }
            return;
        }
        jwtToken = authHeader.substring(7);
        var storedToken = tokenRepository.findByRefreshTokenAndRevokedFalseAndExpiredFalse(jwtToken)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("text/plain");
            try {
                response.getWriter().write("Logged out successfully");
            } catch (IOException e) {
                logger.error("Error writing unauthorized response", e);
            }
        }
    }
}
