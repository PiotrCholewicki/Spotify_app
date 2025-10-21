package com.example.spotify_stats.api.controller;

import com.example.spotify_stats.api.config.SpotifyConfiguration;
import com.example.spotify_stats.api.dto.ArtistDTO;
import com.example.spotify_stats.api.dto.SongDTO;
import com.example.spotify_stats.api.persistence.entity.UserDetails;
import com.example.spotify_stats.api.persistence.repository.ArtistRepository;
import com.example.spotify_stats.api.persistence.repository.UserDetailsRepository;
import com.example.spotify_stats.api.service.UserProfileService;
import com.example.spotify_stats.api.service.UserTopArtistsService;
import com.example.spotify_stats.api.service.UserTopTracksService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.detailed.ForbiddenException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ApiController {
    @Value("${custom.server.ip}")
    private String customIp;

    @Autowired
    SpotifyConfiguration spotifyConfiguration;

    @Autowired
    UserTopArtistsService userTopArtistsService;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    UserTopTracksService userTopTracksService;

    @GetMapping("login")
    public String spotifyLogin(){
        SpotifyApi object = spotifyConfiguration.getSpotifyObject();

        AuthorizationCodeUriRequest authorizationCodeUriRequest = object.authorizationCodeUri()
                .scope("user-read-private user-read-email user-top-read user-library-read user-library-modify")
                .show_dialog(true)
                .redirect_uri(object.getRedirectURI())
                .build();

        final URI uri = authorizationCodeUriRequest.execute();
        System.out.println("Spotify login redirect: " + uri);
        return uri.toString();
    }
    @GetMapping("get-user-code")
    public void getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        SpotifyApi spotifyApi = spotifyConfiguration.createSpotifyApi(); //new object for the session
        User user = null;

        try {
            AuthorizationCodeCredentials authCode = spotifyApi.authorizationCode(userCode).build().execute();
            spotifyApi.setAccessToken(authCode.getAccessToken());
            spotifyApi.setRefreshToken(authCode.getRefreshToken());

            user = spotifyApi.getCurrentUsersProfile().build().execute();
            userProfileService.insertOrUpdateUserDetails(user, authCode.getAccessToken(), authCode.getRefreshToken());
            System.out.println("Saving user to DB: " + user.getId());

            response.sendRedirect(customIp + "/home?userId=" + user.getId());
        } catch (ForbiddenException e) {
            System.out.println("Spotify forbidden for this user: " + e.getMessage());
            response.sendRedirect(customIp + "/login?error=forbidden");
        } catch (Exception e) {
            System.out.println("Exception occured while getting user code " + e);
            response.sendRedirect(customIp + "/login?error=unknown");
        }
    }


    @GetMapping(value = "home")
    public String home(@RequestParam String userId){

        try {

            return userId;
        }
        catch (Exception e) {
            System.out.println("Exception occurred while landing to home page: " + e);
        }
        return null;
    }
    //function for displaying the most streamed song of logged user

    @GetMapping(value = "user-top-tracks")
    public List<SongDTO> userTopTracks(@RequestParam String userId, @RequestParam(defaultValue = "medium_term") String time_range){

        return userTopTracksService.getSongDTO(userId, time_range);
    }

    @GetMapping(value = "user-top-tracks/by-artist")
    public List<SongDTO> userTopTracksByArtist(@RequestParam String userId, @RequestParam String artist){
        return userTopTracksService.getUserTopTracksByArtist(userId, artist);
    }
    @GetMapping(value = "user-top-artists")
    public List<ArtistDTO> userTopArtist(@RequestParam String userId, @RequestParam(defaultValue = "medium_term") String time_range){

        return userTopArtistsService.getArtistDTO(userId, time_range);
    }

}
