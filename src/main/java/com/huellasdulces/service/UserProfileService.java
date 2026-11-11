package com.huellasdulces.service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.huellasdulces.dto.UserProfileAvatar;
import com.huellasdulces.dto.UserProfileResponse;
import com.huellasdulces.dto.UserProfileUpdateRequest;
import com.huellasdulces.model.UserProfile;
import com.huellasdulces.repository.UserProfileRepository;

@Service
@Transactional
public class UserProfileService {

    private static final long MAX_AVATAR_SIZE = 1_048_576L;

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    public UserProfileResponse getProfile(String username) {
        return toResponse(getOrCreate(username));
    }

    public UserProfileResponse updateProfile(
            String username,
            UserProfileUpdateRequest request) {

        UserProfile profile = getOrCreate(username);

        profile.setDisplayName(request.displayName());
        profile.setEmail(request.email());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());

        return toResponse(repository.save(profile));
    }

    public UserProfileResponse uploadAvatar(String username, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debes seleccionar una imagen.");
        }

        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El avatar no puede superar 1 MB.");
        }

        if (!"image/webp".equalsIgnoreCase(file.getContentType())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El avatar debe estar en formato WebP.");
        }

        try {
            byte[] data = file.getBytes();

            if (!isWebP(data)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El archivo no es una imagen WebP válida.");
            }

            UserProfile profile = getOrCreate(username);
            profile.setAvatar(data, "image/webp");

            return toResponse(repository.save(profile));

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No se pudo leer la imagen.", exception);
        }
    }

    @Transactional(readOnly = true)
    public UserProfileAvatar getAvatar(String username) {
        UserProfile profile = repository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe el perfil."));

        if (profile.getAvatarData() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El usuario todavía no tiene avatar.");
        }

        return new UserProfileAvatar(
                profile.getAvatarData(),
                profile.getAvatarContentType()
        );
    }

    public void deleteAvatar(String username) {
        UserProfile profile = getOrCreate(username);
        profile.removeAvatar();
        repository.save(profile);
    }

    private UserProfile getOrCreate(String username) {
        return repository.findByUsername(username)
                .orElseGet(() -> repository.save(new UserProfile(username)));
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getAvatarData() != null,
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    private boolean isWebP(byte[] data) {
        return data.length >= 12
                && data[0] == 'R'
                && data[1] == 'I'
                && data[2] == 'F'
                && data[3] == 'F'
                && data[8] == 'W'
                && data[9] == 'E'
                && data[10] == 'B'
                && data[11] == 'P';
    }
}