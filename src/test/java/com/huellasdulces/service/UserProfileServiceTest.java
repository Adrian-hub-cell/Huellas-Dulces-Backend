
package com.huellasdulces.service;

import com.huellasdulces.dto.UserProfileAvatar;
import com.huellasdulces.dto.UserProfileResponse;
import com.huellasdulces.dto.UserProfileUpdateRequest;
import com.huellasdulces.model.UserProfile;
import com.huellasdulces.repository.UserProfileRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    private UserProfileRepository repository;
    private UserProfileService service;

    @BeforeEach
    void setUp() {
        repository = mock(UserProfileRepository.class);
        service = new UserProfileService(repository);
    }

    @Test
    void getProfileShouldCreateProfileWhenItDoesNotExist() {
        when(repository.findByUsername("adrian"))
                .thenReturn(Optional.empty());

        when(repository.save(any(UserProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileResponse response = service.getProfile("adrian");

        assertThat(response.username()).isEqualTo("adrian");
        assertThat(response.hasAvatar()).isFalse();

        verify(repository).save(any(UserProfile.class));
    }

    @Test
    void getProfileShouldReturnExistingProfile() {
        UserProfile profile = new UserProfile("adrian");
        profile.setDisplayName("Adrian");

        when(repository.findByUsername("adrian"))
                .thenReturn(Optional.of(profile));

        UserProfileResponse response = service.getProfile("adrian");

        assertThat(response.username()).isEqualTo("adrian");
        assertThat(response.displayName()).isEqualTo("Adrian");
        verify(repository, never()).save(any(UserProfile.class));
    }

    @Test
    void updateProfileShouldSaveUpdatedInformation() {
        UserProfile profile = new UserProfile("adrian");

        when(repository.findByUsername("adrian"))
                .thenReturn(Optional.of(profile));

        when(repository.save(any(UserProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "Adrian Gonzalez",
                "adrian@example.com",
                "3001234567",
                "Calle 10"
        );

        UserProfileResponse response =
                service.updateProfile("adrian", request);

        assertThat(response.displayName()).isEqualTo("Adrian Gonzalez");
        assertThat(response.email()).isEqualTo("adrian@example.com");
        assertThat(response.phone()).isEqualTo("3001234567");
        assertThat(response.address()).isEqualTo("Calle 10");

        verify(repository).save(profile);
    }

    @Test
    void uploadAvatarShouldSaveValidWebPImage() throws IOException {
        UserProfile profile = new UserProfile("adrian");

        byte[] webp = new byte[] {
                'R', 'I', 'F', 'F',
                0, 0, 0, 0,
                'W', 'E', 'B', 'P'
        };

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.webp",
                "image/webp",
                webp
        );

        when(repository.findByUsername("adrian"))
                .thenReturn(Optional.of(profile));

        when(repository.save(any(UserProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileResponse response =
                service.uploadAvatar("adrian", file);

        assertThat(response.hasAvatar()).isTrue();
        assertThat(profile.getAvatarData()).containsExactly(webp);
        assertThat(profile.getAvatarContentType()).isEqualTo("image/webp");
    }

    @Test
    void uploadAvatarShouldRejectNullFile() {
        assertThatThrownBy(() -> service.uploadAvatar("adrian", null))
                .isInstanceOf(ResponseStatusException.class);

        verifyNoInteractions(repository);
    }

    @Test
    void uploadAvatarShouldRejectEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.webp", "image/webp", new byte[0]
        );

        assertThatThrownBy(() -> service.uploadAvatar("adrian", file))
                .isInstanceOf(ResponseStatusException.class);

        verifyNoInteractions(repository);
    }

    @Test
    void uploadAvatarShouldRejectFileLargerThanOneMegabyte() {
        byte[] data = new byte[1_048_577];

        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.webp", "image/webp", data
        );

        assertThatThrownBy(() -> service.uploadAvatar("adrian", file))
                .isInstanceOf(ResponseStatusException.class);

        verifyNoInteractions(repository);
    }

    @Test
    void uploadAvatarShouldRejectWrongContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", new byte[] {1, 2, 3}
        );

        assertThatThrownBy(() -> service.uploadAvatar("adrian", file))
                .isInstanceOf(ResponseStatusException.class);

        verifyNoInteractions(repository);
    }

    @Test
    void uploadAvatarShouldRejectInvalidWebPSignature() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.webp",
                "image/webp",
                new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}
        );

        assertThatThrownBy(() -> service.uploadAvatar("adrian", file))
                .isInstanceOf(ResponseStatusException.class);

        verifyNoInteractions(repository);
    }

    @Test
    void getAvatarShouldReturnStoredAvatar() {
        UserProfile profile = new UserProfile("adrian");
        byte[] data = new byte[] {1, 2, 3};

        profile.setAvatar(data, "image/webp");

        when(repository.findByUsername("adrian"))
                .thenReturn(Optional.of(profile));

        UserProfileAvatar response = service.getAvatar("adrian");

        assertThat(response.data()).containsExactly(data);
        assertThat(response.contentType()).isEqualTo("image/webp");
    }

    @Test
    void getAvatarShouldThrowWhenProfileDoesNotExist() {
        when(repository.findByUsername("adrian"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAvatar("adrian"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getAvatarShouldThrowWhenAvatarDoesNotExist() {
        UserProfile profile = new UserProfile("adrian");

        when(repository.findByUsername("adrian"))
                .thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> service.getAvatar("adrian"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteAvatarShouldRemoveStoredAvatar() {
        UserProfile profile = new UserProfile("adrian");
        profile.setAvatar(new byte[] {1, 2, 3}, "image/webp");

        when(repository.findByUsername("adrian"))
                .thenReturn(Optional.of(profile));

        service.deleteAvatar("adrian");

        assertThat(profile.getAvatarData()).isNull();
        assertThat(profile.getAvatarContentType()).isNull();

        verify(repository).save(profile);
    }

    @Test
    void deleteAvatarShouldCreateProfileWhenItDoesNotExist() {
        when(repository.findByUsername("adrian"))
                .thenReturn(Optional.empty());

        when(repository.save(any(UserProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.deleteAvatar("adrian");

        verify(repository, times(2)).save(any(UserProfile.class));
    }
}
