package com.huellasdulces.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileAvatarTest {

    @Test
    void equalsShouldReturnTrueForSameInstance() {
        UserProfileAvatar avatar =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/webp");

        assertThat(avatar).isEqualTo(avatar);
    }

    @Test
    void equalsShouldReturnTrueForDifferentInstancesWithSameContent() {
        UserProfileAvatar first =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/webp");
        UserProfileAvatar second =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/webp");

        assertThat(first).isEqualTo(second);
    }

    @Test
    void equalsShouldReturnFalseForNullOrDifferentType() {
        UserProfileAvatar avatar =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/webp");

        assertThat(avatar.equals(null)).isFalse();
        assertThat(avatar.equals("avatar")).isFalse();
    }

    @Test
    void equalsShouldReturnFalseWhenDataIsDifferent() {
        UserProfileAvatar first =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/webp");
        UserProfileAvatar second =
                new UserProfileAvatar(new byte[] {1, 2, 4}, "image/webp");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equalsShouldReturnFalseWhenContentTypeIsDifferent() {
        UserProfileAvatar first =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/webp");
        UserProfileAvatar second =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/png");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void hashCodeShouldBeEqualForSameContent() {
        UserProfileAvatar first =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/webp");
        UserProfileAvatar second =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/webp");

        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void toStringShouldIncludeAvatarDataAndContentType() {
        UserProfileAvatar avatar =
                new UserProfileAvatar(new byte[] {1, 2, 3}, "image/webp");

        assertThat(avatar.toString())
                .isEqualTo("UserProfileAvatar[data=[1, 2, 3], contentType=image/webp]");
    }
}