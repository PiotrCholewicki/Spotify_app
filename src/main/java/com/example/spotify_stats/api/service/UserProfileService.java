package com.example.spotify_stats.api.service;


import com.example.spotify_stats.api.persistence.entity.UserDetails;
import com.example.spotify_stats.api.persistence.repository.SongRepository;
import com.example.spotify_stats.api.persistence.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.util.Objects;

@Service
public class UserProfileService {
    @Autowired
    private UserDetailsRepository userDetailsRepository;

    public UserDetails insertOrUpdateUserDetails(User user, String accessToken, String refreshToken){
        UserDetails userDetails = userDetailsRepository.findByRefId(user.getId());
        if(Objects.isNull(userDetails)){
            userDetails = new UserDetails();
        }
        userDetails.setUserName(user.getDisplayName());
        userDetails.setEmailId(user.getEmail());
        userDetails.setAccessToken(accessToken);
        userDetails.setRefreshToken(refreshToken);
        userDetails.setRefId(user.getId());
        return userDetailsRepository.save(userDetails);
    }

}
