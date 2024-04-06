package com.bba;


import java.net.URI;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;



@RestController
@RequestMapping("/api")
public class AuthController {
    
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/api/get-user-code/");
    private String code = "";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
        .setClientId("e8f4bd36afac4b05903fca8a2240856c")
        .setClientSecret("364f398853df4f6d83b678d0538fed03")
        .setRedirectUri(redirectUri)
        .build();

    @GetMapping("login")
    @ResponseBody
    public String spotifyLogin(){
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
            .scope("user-read-private, user-read-email, user-top-read")
            //prompts for telling user that app is requesting access
            .show_dialog(true)
            .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();  
    }

}
