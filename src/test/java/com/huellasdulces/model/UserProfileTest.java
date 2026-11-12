
package com.huellasdulces.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    void shouldCreateProfileWithEmptyConstructor() {
        UserProfile profile = new UserProfile();

        assertNull(profile.getId());
        assertNull(profile.getUsername());
        assertNull(profile.getDisplayName());
        assertNull(profile.getEmail());
        assertNull(profile.getPhone());
        assertNull(profile.getAddress());
        assertNull(profile.getAvatarData());
        assertNull(profile.getAvatarContentType());
        assertNull(profile.getCreatedAt());
        assertNull(profile.getUpdatedAt());
    }

    @Test
    void shouldCreateProfileWithUsername() {
        UserProfile profile = new UserProfile("adrian");

        assertEquals("adrian", profile.getUsername());
    }

    @Test
    void shouldUpdateAndRetrieveProfileFields() {
        UserProfile profile = new UserProfile("adrian");

        profile.setDisplayName("Adrian");
        profile.setEmail("adrian@example.com");
        profile.setPhone("3001234567");
        profile.setAddress("Medellin, Colombia");

        assertEquals("adrian", profile.getUsername());
        assertEquals("Adrian", profile.getDisplayName());
        assertEquals("adrian@example.com", profile.getEmail());
        assertEquals("3001234567", profile.getPhone());
        assertEquals("Medellin, Colombia", profile.getAddress());
    }

    @Test
    void shouldSetAndRemoveAvatar() {
        UserProfile profile = new UserProfile("adrian");
        byte[] avatar = {1, 2, 3, 4};

        profile.setAvatar(avatar, "image/png");

        assertArrayEquals(avatar, profile.getAvatarData());
        assertEquals("image/png", profile.getAvatarContentType());

        profile.removeAvatar();

        assertNull(profile.getAvatarData());
        assertNull(profile.getAvatarContentType());
    }

    @Test
    void shouldCreateTimestampsOnPersist() throws Exception {
        UserProfile profile = new UserProfile("adrian");

        Method onCreate = UserProfile.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);

        LocalDateTime before = LocalDateTime.now();
        onCreate.invoke(profile);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(profile.getCreatedAt());
        assertNotNull(profile.getUpdatedAt());

        assertFalse(profile.getCreatedAt().isBefore(before));
        assertFalse(profile.getCreatedAt().isAfter(after));

        assertEquals(profile.getCreatedAt(), profile.getUpdatedAt());
    }

    @Test
    void shouldUpdateTimestampOnUpdate() throws Exception {
        UserProfile profile = new UserProfile("adrian");

        Method onCreate = UserProfile.class.getDeclaredMethod("onCreate");
        Method onUpdate = UserProfile.class.getDeclaredMethod("onUpdate");

        onCreate.setAccessible(true);
        onUpdate.setAccessible(true);

        onCreate.invoke(profile);

        LocalDateTime originalCreatedAt = profile.getCreatedAt();
        LocalDateTime originalUpdatedAt = profile.getUpdatedAt();

        Thread.sleep(2);

        onUpdate.invoke(profile);

        assertEquals(originalCreatedAt, profile.getCreatedAt());
        assertTrue(profile.getUpdatedAt().isAfter(originalUpdatedAt));
    }
}
