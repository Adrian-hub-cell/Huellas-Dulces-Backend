package com.huellasdulces.dto;

import java.util.Arrays;
import java.util.Objects;

public record UserProfileAvatar(byte[] data, String contentType) {

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof UserProfileAvatar other)) {
            return false;
        }

        return Arrays.equals(data, other.data)
                && Objects.equals(contentType, other.contentType);
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(data) + Objects.hashCode(contentType);
    }

    @Override
    public String toString() {
        return "UserProfileAvatar[data=" + Arrays.toString(data)
                + ", contentType=" + contentType + "]";
    }
}
