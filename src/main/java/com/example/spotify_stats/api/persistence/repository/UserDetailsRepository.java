package com.example.spotify_stats.api.persistence.repository;

import com.example.spotify_stats.api.persistence.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {
    UserDetails findByRefId(String id);
}
