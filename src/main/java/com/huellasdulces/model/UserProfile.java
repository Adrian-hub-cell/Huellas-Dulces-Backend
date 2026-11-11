package com.huellasdulces.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "display_name", length = 150)
    private String displayName;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(name = "avatar_data")
    private byte[] avatarData;

    @Column(name = "avatar_content_type", length = 50)
    private String avatarContentType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserProfile() {
    }

    public UserProfile(String username) {
        this.username = username;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public byte[] getAvatarData() { return avatarData; }
    public String getAvatarContentType() { return avatarContentType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }

    public void setAvatar(byte[] data, String contentType) {
        this.avatarData = data;
        this.avatarContentType = contentType;
    }

    public void removeAvatar() {
        this.avatarData = null;
        this.avatarContentType = null;
    }
}