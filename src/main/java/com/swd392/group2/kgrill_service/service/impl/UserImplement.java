package com.swd392.group2.kgrill_service.service.impl;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.swd392.group2.kgrill_model.model.Token;
import com.swd392.group2.kgrill_model.model.User;

import com.swd392.group2.kgrill_model.repository.RoleRepository;
import com.swd392.group2.kgrill_model.repository.TokenRepository;
import com.swd392.group2.kgrill_model.repository.UserRepository;
import com.swd392.group2.kgrill_service.dto.CustomUserProfile;
import com.swd392.group2.kgrill_service.dto.UserProfileDto;
import com.swd392.group2.kgrill_service.dto.UserProfileResponse;
import com.swd392.group2.kgrill_service.exception.CustomSuccessHandler;
import com.swd392.group2.kgrill_service.service.JwtService;
import com.swd392.group2.kgrill_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserImplement implements UserService {
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

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

        JWTClaimsSet decryptedClaims;
        try {
            decryptedClaims = jwtService.decryptJwt(token);
        } catch (JOSEException | ParseException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to decrypt JWT token");
        }

        String username = decryptedClaims.getSubject();
        var user = userRepository.findByEmail(username).orElse(null);
        if (user == null || !jwtService.isEncryptedTokenValid(decryptedClaims, user) || accessToken.isRevoked() || accessToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token has expired or been revoked");
        }

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved user information", user);
    }

    @Override
    public ResponseEntity<Object> getAllUsersByAdmin(int pageNo, int pageSize, String sortBy, String sortDir, String email) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<User> UserContent = userRepository.findByEmail(email, pageable);
        List<User> UserList = UserContent.getContent();
        List<CustomUserProfile> content = UserList.stream().map(UserProfile -> modelMapper.map(UserProfile, CustomUserProfile.class)).collect(Collectors.toList());

        for (CustomUserProfile userProfile : content) {
            if (userProfile.getRole().equals("1")) {
                userProfile.setRole("USER");
            } else if (userProfile.getRole().equals("2")) {
                userProfile.setRole("MANAGER");
            } else if (userProfile.getRole().equals("3")) {
                userProfile.setRole("SHIPPER");
            } else if (userProfile.getRole().equals("4")) {
                userProfile.setRole("ADMIN");
            }
        }

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setContent(content);
        userProfileResponse.setPageNo(UserContent.getNumber());
        userProfileResponse.setPageSize(UserContent.getSize());
        userProfileResponse.setTotalElements(UserContent.getTotalElements());
        userProfileResponse.setTotalPages(UserContent.getTotalPages());
        userProfileResponse.setLast(UserContent.isLast());
        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved user information", userProfileResponse);
    }

    @Override
    public ResponseEntity<Object> updateUserProfileByAdmin(UUID id, CustomUserProfile customUserProfile) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {

            if (customUserProfile.getFirstName() != null) {
                user.setFirstName(customUserProfile.getFirstName());
            }
            if (customUserProfile.getLastName() != null) {
                user.setLastName(customUserProfile.getLastName());
            }
            if (customUserProfile.getAddress() != null) {
                user.setAddress(customUserProfile.getAddress());
            }
            if (customUserProfile.getEmail() != null) {
                user.setEmail(customUserProfile.getEmail());
            }
            if (customUserProfile.isAccountNotLocked() != user.isAccountNotLocked()) {
                user.setAccountNotLocked(customUserProfile.isAccountNotLocked());
            }
            if (customUserProfile.getLatitude() != null) {
                user.setLatitude(customUserProfile.getLatitude());
            }

            if (customUserProfile.getLongitude() != null) {
                user.setLongitude(customUserProfile.getLongitude());
            }

            if (customUserProfile.getGender() != null) {
                user.setGender(customUserProfile.getGender());
            }

            if (customUserProfile.getPhone() != null) {
                user.setPhone(customUserProfile.getPhone());
            }

            if (customUserProfile.getRole().equals("USER")) {
                user.setRole(roleRepository.findById(1L).orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized")));
            } else if (customUserProfile.getRole().equals("MANAGER")) {
                user.setRole(roleRepository.findById(2L).orElseThrow(() -> new IllegalStateException("ROLE MANAGER was not initialized")));
            } else if (customUserProfile.getRole().equals("SHIPPER")) {
                user.setRole(roleRepository.findById(3L).orElseThrow(() -> new IllegalStateException("ROLE SHIPPER was not initialized")));
            }

            User updateUser = userRepository.save(user);
            return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "user profile update successfully", updateUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update failed");
        }
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
            if (userProfileDto.getFirstName() != null) {
                user.setFirstName(userProfileDto.getFirstName());
            }
            if (userProfileDto.getLastName() != null) {
                user.setLastName(userProfileDto.getLastName());
            }
            if (userProfileDto.getAddress() != null) {
                user.setAddress(userProfileDto.getAddress());
            }

            if (userProfileDto.getLatitude() != null) {
                user.setLatitude(userProfileDto.getLatitude());
            }

            if (userProfileDto.getLongitude() != null) {
                user.setLongitude(userProfileDto.getLongitude());
            }

            if (userProfileDto.getGender() != null) {
                user.setGender(userProfileDto.getGender());
            }

            if (userProfileDto.getPhone() != null) {
                user.setPhone(userProfileDto.getPhone());
            }
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
