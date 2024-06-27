package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_service.dto.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import com.swd392.group2.kgrill_service.dto.UserProfileDto;

import java.util.UUID;

public interface UserService {
    ResponseEntity<Object> getUserInformation(HttpServletRequest request);

    UserProfileResponse getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir, String email);

    UserProfileDto updateUserInformation(UUID id, UserProfileDto userProfileDto);

    void updateUserProfilePicture(UUID id, String profilePictureUrl);


}
