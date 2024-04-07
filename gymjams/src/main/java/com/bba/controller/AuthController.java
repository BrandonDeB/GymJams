package com.bba.controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class AuthController {

    public static HashMap<String, WorkoutUser> userMap = new HashMap<String, WorkoutUser>();
    public static HashMap<String, String> logins = new HashMap<String, String>();

    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/api/get-user-code/");
    public String code = "";

    public static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId("e8f4bd36afac4b05903fca8a2240856c")
            .setClientSecret("364f398853df4f6d83b678d0538fed03")
            .setRedirectUri(redirectUri)
            .build();
 
    @GetMapping(value="/login")
    public String login(@RequestParam("name") String name, @RequestParam("password") String password, HttpSession session, HttpServletResponse response){
        if(logins.containsKey(name)) {
            if (logins.get(name).equals(password)) {
                session.setAttribute("username", name);
                session.setAttribute("password", password);
                try {
                    response.sendRedirect("http://localhost:8080/scrollingpage.html");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return logins.get(name);
            } else {return "loginFailed";}
        } else {
            try {
                response.sendRedirect("http://localhost:8080/loginpage.html");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "Username does not exist";
        }
    }

    @GetMapping(value="/signup")
    public String signup(@RequestParam("name") String name, @RequestParam("password") String password, HttpServletResponse response) {
        if (logins.containsKey(name)) {
            try {
                response.sendRedirect("http://localhost:8080/loginpage.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Account Already Exists";
        } else { 
            try {
                response.sendRedirect("http://localhost:8080/loginpage.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            logins.put(name, password);
            return "success";
        }
    }

    @GetMapping(value="get-current-user")
    public String currentUser(HttpSession session){
        System.out.println((String) session.getAttribute("username"));
        if (((String) session.getAttribute("username")).equals("")) { 
            return "User not found";   
        } else {
            return (String) session.getAttribute("username");
        }
    }
    
    @GetMapping(value = "get-user-exercises")
    public ArrayList<Exercise> getUserExercises(@RequestParam("name") String name) {
        ArrayList<Exercise> list = userMap.get(name).exercises;
        return list;
    }

    @GetMapping(value = "add-user-workout")
    public String addUserExercises(@RequestParam("reps") String reps, @RequestParam("sets") String sets, @RequestParam("exercise") String exercise, @RequestParam("weight") String weight, @RequestParam("link") String link, HttpSession session, HttpServletResponse response) {
        
        String currentUser = (String) session.getAttribute("username");
        System.out.println(reps + sets + exercise + weight + currentUser + link);
        String[] rep = reps.split(",");
        String[] set = sets.split(",");
        String[] exer = exercise.split(",");
        String[] weigh = weight.split(",");;
        for(int i = 0; i < rep.length; i++) {
                if (!userMap.containsKey(currentUser)) {
                    userMap.put(currentUser, new WorkoutUser(new ArrayList<Exercise>()));
                }
                userMap.get(currentUser)
                    .addExercise(new Exercise(exer[i], Integer.parseInt(weigh[i]), Integer.parseInt(rep[i]), Integer.parseInt(set[i]), new Date(), link));
                System.out.println("Adding exercise" + new Date().toString());
        }
        try {
            response.sendRedirect("http://localhost:8080/profilepage.html");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return currentUser;
        //return userMap.get("").exercises;
    }
}
