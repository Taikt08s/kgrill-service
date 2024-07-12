package com.swd392.group2.kgrill_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nimbusds.jose.JOSEException;
import com.swd392.group2.kgrill_model.enums.AuthenticationProvider;
import com.swd392.group2.kgrill_model.enums.EmailTemplateName;
import com.swd392.group2.kgrill_model.enums.TokenType;
import com.swd392.group2.kgrill_model.model.EmailToken;
import com.swd392.group2.kgrill_model.model.Token;
import com.swd392.group2.kgrill_model.model.User;
import com.swd392.group2.kgrill_model.repository.EmailTokenRepository;
import com.swd392.group2.kgrill_model.repository.RoleRepository;
import com.swd392.group2.kgrill_model.repository.TokenRepository;
import com.swd392.group2.kgrill_model.repository.UserRepository;
import com.swd392.group2.kgrill_service.config.LogoutServiceConfig;
import com.swd392.group2.kgrill_service.dto.AuthenticationRequest;
import com.swd392.group2.kgrill_service.dto.AuthenticationResponse;
import com.swd392.group2.kgrill_service.dto.GoogleAuthenticationRequest;
import com.swd392.group2.kgrill_service.dto.RegistrationRequest;
import com.swd392.group2.kgrill_service.exception.ActivationTokenException;
import com.swd392.group2.kgrill_service.exception.RefreshTokenNotFoundException;
import com.swd392.group2.kgrill_service.exception.RegistrationAccountExistedException;
import com.swd392.group2.kgrill_service.service.AuthService;
import com.swd392.group2.kgrill_service.service.EmailService;
import com.swd392.group2.kgrill_service.service.JwtService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthImplement implements AuthService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    @Value("${application.mail.secure.characters}")
    private String emailSecureCharacter;
    private static final Logger logger = LoggerFactory.getLogger(LogoutServiceConfig.class);

    @Override
    public void register(RegistrationRequest request) throws MessagingException, UnsupportedEncodingException {
        var userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationAccountExistedException("Account already exists");
        }
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .phone(request.getPhone())
                .gender("")
                .dob("")
                .password(passwordEncoder.encode(request.getPassword()))
                .accountNotLocked(true)
                .enable(false)
                .role(userRole)
                .authProvider(AuthenticationProvider.LOCAL)
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                (String) newToken,
                "Account activation"
        );

    }

    private Object generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode();
        var token = EmailToken.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .revokedToken(false)
                .user(user)
                .build();
        emailTokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode() {
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(emailSecureCharacter.length());
            codeBuilder.append(emailSecureCharacter.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    private void saveUserToken(User user, String jwtAccessToken, String jwtRefreshToken) {
        var token = Token.builder()
                .user(user)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) throws JOSEException {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());

        claims.put("full_name", user.fullName());

        var jwtAccessToken = jwtService.generateEncryptedToken(claims, user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserToken(user);
        saveUserToken(user, jwtAccessToken, jwtRefreshToken);


        return AuthenticationResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    private void revokeAllUserToken(User user) {
        var validUserToken = tokenRepository.findAllValidTokensByUser(user.getUserId());
        if (validUserToken.isEmpty())
            return;
        validUserToken.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);
    }

    @Override
    public void activateAccount(String token, HttpServletResponse response) throws MessagingException, UnsupportedEncodingException {

        EmailToken savedToken = emailTokenRepository.findByToken(token)
                .orElseThrow(() -> new ActivationTokenException("Invalid email token"));

        if (savedToken.getValidateAt() != null) {
            throw new ActivationTokenException("Your account is already activated");
        }

        if (savedToken.isRevokedToken()) {
            throw new ActivationTokenException("This activation code is invalid as it has been revoked. Please use the latest activation code sent to your email.");
        }

        if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
            savedToken.setRevokedToken(true);
            emailTokenRepository.save(savedToken);
            sendValidationEmail(savedToken.getUser());
            throw new ActivationTokenException("Activation code has expired. A new code has been sent to your email address");
        }

        var user = userRepository.findById(savedToken.getUser().getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnable(true);
        userRepository.save(user);
        savedToken.setRevokedToken(true);
        savedToken.setValidateAt(LocalDateTime.now());
        emailTokenRepository.save(savedToken);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("text/plain");
        try {
            response.getWriter().write("Account verification successfully");
        } catch (IOException e) {
            logger.error("Error writing error response", e);
        }

    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, JOSEException {
        final String authHeader = checkInputToken(request, response);
        if (authHeader == null) return;
        final String refreshedToken;
        final String username;
        refreshedToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshedToken);

        final Token currentRefreshedToken = tokenRepository.findByRefreshTokenAndRevokedFalseAndExpiredFalse(refreshedToken).orElseThrow(()
                -> new RefreshTokenNotFoundException("Token not found or is invalid"));

        if (username != null) {
            var user = this.userRepository.findByEmail(username)
                    .orElseThrow();
            if ((jwtService.isTokenValid(refreshedToken, user))
                    && !currentRefreshedToken.isRevoked() && !currentRefreshedToken.isExpired()) {
                var accessToken = jwtService.generateEncryptedToken(new HashMap<>(), user);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshedToken)
                        .build();

                revokeAllUserToken(user);
                saveUserToken(user, accessToken, refreshedToken);

                response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("text/plain");
                try {
                    response.getWriter().write("JWT token has expired and revoked");
                } catch (IOException e) {
                    logger.error("Error writing unauthorized response", e);
                }
            }
        }
    }

    private static String checkInputToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("text/plain");
            try {
                response.getWriter().write("No JWT token found in the request header");
            } catch (IOException e) {
                logger.error("Error writing unauthorized response", e);
            }
            return null;
        }
        return authHeader;
    }

    @Override
    public AuthenticationResponse findOrCreateUser(GoogleAuthenticationRequest request) throws JOSEException {
        var userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));
        var optionalUser = userRepository.findByEmail(request.getEmail());
        User user = new User();
        if (optionalUser.isEmpty()) {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setRole(userRole);
            user.setAddress("");
            user.setPhone("");
            user.setGender("");
            user.setDob("");
            user.setProfilePic(request.getPhotoUrl());
            user.setEnable(true);
            user.setAccountNotLocked(true);
            user.setAuthProvider(AuthenticationProvider.GOOGLE);
            userRepository.save(user);
        } else {
            user = optionalUser.get();
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }
        var extraClaimsGoogle = new HashMap<String, Object>();
        extraClaimsGoogle.put("full_name", user.fullName());

        String jwtAccessToken = jwtService.generateEncryptedToken(extraClaimsGoogle, user);
        String jwtRefreshToken = jwtService.generateRefreshToken(user);

        logger.info(jwtAccessToken);
        logger.info(jwtRefreshToken);

        revokeAllUserToken(user);
        saveUserToken(user, jwtAccessToken, jwtRefreshToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }
}
