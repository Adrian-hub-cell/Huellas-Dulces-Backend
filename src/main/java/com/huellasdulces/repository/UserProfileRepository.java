package com.huellasdulces.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.huellasdulces.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUsername(String username);
}