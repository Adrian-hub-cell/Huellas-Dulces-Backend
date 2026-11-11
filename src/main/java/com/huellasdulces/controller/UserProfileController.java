package com.huellasdulces.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import com.huellasdulces.dto.UserProfileAvatar;
import com.huellasdulces.dto.UserProfileResponse;
import com.huellasdulces.dto.UserProfileUpdateRequest;
import com.huellasdulces.service.UserProfileService;

@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(Authentication authentication) {
        return service.getProfile(authentication.getName());
    }

    @PutMapping("/me")
    public UserProfileResponse updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateRequest request) {

        return service.updateProfile(authentication.getName(), request);
    }

    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserProfileResponse uploadMyAvatar(
            Authentication authentication,
            @RequestPart("file") MultipartFile file) {

        return service.uploadAvatar(authentication.getName(), file);
    }

    @GetMapping("/me/avatar")
    public ResponseEntity<byte[]> getMyAvatar(Authentication authentication) {
        UserProfileAvatar avatar = service.getAvatar(authentication.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(avatar.contentType()))
                .cacheControl(CacheControl.noCache())
                .header("X-Content-Type-Options", "nosniff")
                .body(avatar.data());
    }

    @DeleteMapping("/me/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyAvatar(Authentication authentication) {
        service.deleteAvatar(authentication.getName());
    }
}