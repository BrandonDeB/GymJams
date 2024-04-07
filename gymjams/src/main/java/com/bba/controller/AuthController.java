package com.bba.controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
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
    }

    @GetMapping(value="/logout")
    public String logout(HttpSession session, HttpServletResponse response){
        session.removeAttribute("username");
        session.removeAttribute("password");
        try {
            response.sendRedirect("http://localhost:8080/loginpage.html");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "Success";
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
                logins.put(name, password);
                userMap.put(name, new WorkoutUser(new ArrayList<Workout>()));
                response.sendRedirect("http://localhost:8080/loginpage.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    public ArrayList<Workout> getUserExercises(@RequestParam("name") String name) {
        ArrayList<Workout> list = userMap.get(name).workout;
        return list;
    }

    @GetMapping(value = "add-user-workout")
    public String addUserExercises(@RequestParam("reps") String reps, @RequestParam("sets") String sets, @RequestParam("exercise") String exercise, @RequestParam("weight") String weight, @RequestParam("link") String link, HttpSession session, HttpServletResponse response) {
        
        String currentUser = (String) session.getAttribute("username");
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
        String currentUser = (String) session.getAttribute("username");
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
        String currentUser = (String) session.getAttribute("username");
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
            String currentUser = (String) session.getAttribute("username");
            if (userMap.containsKey(currentUser)) {
                for (String friend : userMap.get(currentUser).friends) {
                    if (userMap.get(friend).friends.contains(currentUser)) {
                        allFriends.addAll(userMap.get(friend).workout);
                    }
                }
            
            System.out.println(currentUser);
            allFriends.addAll(userMap.get(currentUser).workout);
            Collections.sort(allFriends, new Comparator<Workout>() {
                public int compare(Workout o1, Workout o2) {
                    return o1.date.compareTo(((Workout) o2).date);
                }
            });
        }

        return allFriends;
    }
}
