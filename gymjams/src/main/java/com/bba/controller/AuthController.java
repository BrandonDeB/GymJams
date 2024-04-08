package com.bba.controller;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistCoverImageRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetUsersProfileRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class AuthController {


    public static HashMap<String, WorkoutUser> userMap;
    public static HashMap<String, String> logins;

    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/api/spotify-login");
    public String code ;

    public static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
    .setClientId("")
    .setClientSecret("")
    .setRedirectUri(redirectUri)
    .build();
    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();;


    public AuthController() {
        userMap = new HashMap<String, WorkoutUser>();
        logins = new HashMap<String, String>();

        ClientCredentials clientCredentials;
        try {
            clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value="/spotify-login")
    public String spotifyLogin(@RequestParam("code") String code, HttpSession session, HttpServletResponse response) throws IOException{
        AuthorizationCodeRequest request = spotifyApi.authorizationCode(code).build();
        AuthorizationCodeCredentials credentials;
        try {
            credentials = request.execute();
            
            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());
            if (!userMap.containsKey(getCurrentId())) {
                userMap.put(getCurrentId(), new WorkoutUser(new ArrayList<Workout>()));
            }
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
        response.sendRedirect("http://localhost:8080/profilepage.html");
        return "Temp";
    }

    @GetMapping(value="/spotify-login-link")
    public String spotifyLoginLink(HttpSession session, HttpServletResponse response) throws IOException{
        AuthorizationCodeUriRequest request = spotifyApi.authorizationCodeUri().scope("user-read-email, user-follow-read").show_dialog(true).build();
        final URI uri = request.execute();
        response.sendRedirect(uri.toString());
        return uri.toString();
    }
 
    // @GetMapping(value="/login")
    // public String login(@RequestParam("name") String name, @RequestParam("password") String password, HttpSession session, HttpServletResponse response){
    //     if(logins.containsKey(name)) {
    //         if (logins.get(name).equals(password)) {
    //             session.setAttribute("username", name);
    //             session.setAttribute("password", password);
    //             try {
    //                 response.sendRedirect("http://localhost:8080/scrollingpage.html");
    //             } catch (IOException e) {
    //                 // TODO Auto-generated catch block
    //                 e.printStackTrace();
    //             }
    //             return logins.get(name);
    //         } else {return "loginFailed";}
    //     } else {
    //         try {
    //             response.sendRedirect("http://localhost:8080/loginpage.html");
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //         return "Username does not exist";
    //     }
    // }

    @GetMapping(value="/logout")
    public String logout(HttpSession session, HttpServletResponse response) throws IOException{
        spotifyApi.setAccessToken("");
        spotifyApi.setRefreshToken("");
        response.sendRedirect("http://localhost:8080/loginpage.html");
        return "done";
    }

    @GetMapping(value="/get-profile-picture") 
    public String getProfilePicture(HttpSession session, HttpServletResponse response) throws IOException{
        GetUsersProfileRequest profileReq = spotifyApi.getUsersProfile(getCurrentId()).build();
        User user;
        try {
            user = profileReq.execute();
            return user.getImages()[0].getUrl();
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // @GetMapping(value="/signup")
    // public String signup(@RequestParam("name") String name, @RequestParam("password") String password, HttpServletResponse response) {
    //     if (logins.containsKey(name)) {
    //         try {
    //             response.sendRedirect("http://localhost:8080/loginpage.html");
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //         return "Account Already Exists";
    //     } else { 
    //         try {
    //             logins.put(name, password);
    //             userMap.put(name, new WorkoutUser(new ArrayList<Workout>()));
    //             response.sendRedirect("http://localhost:8080/loginpage.html");
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //         return "success";
    //     }
    // }

    @GetMapping(value="get-current-user")
    public String currentUser(HttpSession session){
        return getCurrentDisplayName();
    }
    
    @GetMapping(value = "get-user-exercises")
    public ArrayList<Workout> getUserExercises() {
        ArrayList<Workout> list = userMap.get(getCurrentId()).workout;
        return list;
    }

    @GetMapping(value = "get-playlist-picture")
    public String getPlaylistPicture(@RequestParam("link") String link) throws ParseException, SpotifyWebApiException, IOException {
        link = link.replace("https://open.spotify.com/playlist/", "");
        link = link.substring(0, link.indexOf("?"));
        System.out.println(link);
        GetPlaylistCoverImageRequest request = spotifyApi.getPlaylistCoverImage(link).build();
        return request.execute()[0].getUrl();
    }

    @GetMapping(value = "add-user-workout")
    public String addUserExercises(@RequestParam("reps") String reps, @RequestParam("sets") String sets, @RequestParam("exercise") String exercise, @RequestParam("weight") String weight, @RequestParam("link") String link, HttpSession session, HttpServletResponse response) {
        
        String currentUser = getCurrentId();
        System.out.println(reps + sets + exercise + weight + currentUser + link);
        String[] rep = reps.split(",");
        String[] set = sets.split(",");
        String[] exer = exercise.split(",");
        String[] weigh = weight.split(",");
        Workout workout = new Workout(link, currentUser);
        for(int i = 0; i < rep.length; i++) {
                workout.addExercise(new Exercise(exer[i], Integer.parseInt(weigh[i]), Integer.parseInt(rep[i]), Integer.parseInt(set[i])));
                System.out.println("Adding exercise" + new Date().toString());
        }
        userMap.get(currentUser)
        .addWorkout(workout);
        try {
            response.sendRedirect("http://localhost:8080/profilepage.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentUser;
    }

    @GetMapping(value = "add-friend")
    public String addFriend(@RequestParam("name") String name, HttpSession session, HttpServletResponse response) {
        String currentUser = getCurrentId();
        userMap.get(currentUser).friends.add(name);
        try {
            response.sendRedirect("http://localhost:8080/profilepage.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Added Friend";
    }

    @GetMapping(value = "remove-friend")
    public String removeFriend(@RequestParam("name") String name, HttpSession session, HttpServletResponse response) {
        String currentUser = getCurrentId();
        userMap.get(currentUser).friends.remove(name);
        try {
            response.sendRedirect("http://localhost:8080/profilepage.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Removed Friend";
    }

    @GetMapping(value = "get-next-post")
    public ArrayList<Workout> nextPost(HttpSession session, HttpServletResponse response) {
            ArrayList<Workout> allFriends = new ArrayList<>();
            String currentUser = getCurrentId();
            if (userMap.containsKey(currentUser)) {
                for (String friend : userMap.get(currentUser).friends) {
                    if(userMap.containsKey(friend)) {
                        if (userMap.get(friend).friends.contains(currentUser)) {
                            allFriends.addAll(userMap.get(friend).workout);
                        }
                    }
                }
                allFriends.addAll(userMap.get(currentUser).workout);
                Collections.sort(allFriends, new Comparator<Workout>() {
                    public int compare(Workout o1, Workout o2) {
                        return o2.date.compareTo(((Workout) o1).date);
                    }
                });
            }

        return allFriends;
    }

    private String getCurrentDisplayName(){
        GetCurrentUsersProfileRequest profileReq = spotifyApi.getCurrentUsersProfile().build();
        User user;
        try {
            user = profileReq.execute();
            return user.getDisplayName();
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCurrentId(){
        GetCurrentUsersProfileRequest profileReq = spotifyApi.getCurrentUsersProfile().build();
        User user;
        try {
            user = profileReq.execute();
            return user.getId();
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
