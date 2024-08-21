package com.umiverse.umiversebackend.controller;

import com.umiverse.umiversebackend.body.*;
import com.umiverse.umiversebackend.body.ResponseBody;
import com.umiverse.umiversebackend.exception.*;
import com.umiverse.umiversebackend.model.User;
import com.umiverse.umiversebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseBody> registerUser(@RequestBody RegisterRequestBody body) {
        return userService.register(body);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBody> authenticateUser(@RequestBody LoginRequestBody body) {
        return userService.authenticate(body.getUsername(), body.getPassword());
    }

    @PostMapping("/disconnect")
    public ResponseEntity<String> disconnect(@RequestParam int id) {
        userService.disconnect(id);
        return ResponseEntity.ok("User Disconnected");
    }

    @GetMapping("/online")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

    @GetMapping("/details")
    public ResponseEntity<Object> getUserDetails(@RequestParam int id) {
        User user = userService.getUserById(id);
        if(user != null){
             return ResponseEntity.ok().body(
                     new DetailsResponseEntity(user.getUserID(), user.getUsername(), user.getEmail(), user.getFullName(), user.getBio())
             );
        }
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseBody("User not found", 0001));
    }
}
