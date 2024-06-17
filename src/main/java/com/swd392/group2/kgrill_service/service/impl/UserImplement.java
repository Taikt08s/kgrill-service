package com.swd392.group2.kgrill_service.service.impl;

import com.group2.kgrill.dto.UserProfileDto;
import com.group2.kgrill.exception.CustomSuccessHandler;
import com.swd392.group2.kgrill_model.model.Token;
import com.swd392.group2.kgrill_model.model.User;


import com.swd392.group2.kgrill_model.repository.TokenRepository;
import com.swd392.group2.kgrill_model.repository.UserRepository;
import com.swd392.group2.kgrill_service.service.JwtService;
import com.swd392.group2.kgrill_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserImplement implements UserService {
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<Object> getUserInformation(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT token found in the request header");
        }

        final Token accessToken = tokenRepository.findByAccessToken(token).orElse(null);
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        String username = jwtService.extractUsername(token);
        var user = userRepository.findByEmail(username).orElse(null);
        if (user == null || !jwtService.isTokenValid(token, user) || accessToken.isRevoked() || accessToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token has expired and revoked");
        }

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved user information", user);
    }

    public String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public UserProfileDto updateUserInformation(UUID id, UserProfileDto userProfileDto) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFirstName(userProfileDto.getFirstName());
            user.setLastName(userProfileDto.getLastName());
            user.setAddress(userProfileDto.getAddress());
            user.setGender(userProfileDto.getGender());
            user.setPhone(userProfileDto.getPhone());
            user.setLatitude(userProfileDto.getLatitude());
            user.setLongitude(userProfileDto.getLongitude());
            User updateUser = userRepository.save(user);
            return mapToUserProfileDto(updateUser);
        } else {
            return null;
        }
    }

    @Override
    public void updateUserProfilePicture(UUID id, String profilePictureUrl) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setProfilePic(profilePictureUrl);
            User updatedUser = userRepository.save(user);
            mapToUserProfileDto(updatedUser);
        }
    }

    private UserProfileDto mapToUserProfileDto(User user) {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(user.getUserId());
        userProfileDto.setFirstName(user.getFirstName());
        userProfileDto.setLastName(user.getLastName());
        userProfileDto.setAddress(user.getAddress());
        userProfileDto.setGender(user.getGender());
        userProfileDto.setPhone(userProfileDto.getPhone());

        return userProfileDto;
    }
}
