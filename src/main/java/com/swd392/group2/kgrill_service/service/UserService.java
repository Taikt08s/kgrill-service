package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_service.dto.CustomUserProfile;
import com.swd392.group2.kgrill_service.dto.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import com.swd392.group2.kgrill_service.dto.UserProfileDto;

import java.util.UUID;

public interface UserService {
    ResponseEntity<Object> getUserInformation(HttpServletRequest request);

    ResponseEntity<Object> getAllUsersByAdmin(int pageNo, int pageSize, String sortBy, String sortDir, String email);

    ResponseEntity<Object> updateUserProfileByAdmin(UUID id, CustomUserProfile customUserProfile);

    UserProfileDto updateUserInformation(UUID id, UserProfileDto userProfileDto);

    void updateUserProfilePicture(UUID id, String profilePictureUrl);

    ResponseEntity<Object> getNumberOfUsersByRoleId (String roleName);

    boolean saveDeviceToken(UUID userId, String token);

    String getDeviceTokenByUserId(UUID userId);

}
