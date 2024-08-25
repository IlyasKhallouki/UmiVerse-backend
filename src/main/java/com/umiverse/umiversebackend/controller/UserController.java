package com.umiverse.umiversebackend.controller;

import com.umiverse.umiversebackend.body.*;
import com.umiverse.umiversebackend.body.ResponseBody;
import com.umiverse.umiversebackend.exception.*;
import com.umiverse.umiversebackend.model.User;
import com.umiverse.umiversebackend.repository.mysql.UserRepository;
import com.umiverse.umiversebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/auth/register")
    public ResponseEntity<ResponseBody> registerUser(@RequestBody RegisterRequestBody body) {
        return userService.register(body);
    }

    @PostMapping("/auth/verify-user")
    public ResponseEntity<ResponseBody> verifyUser(@RequestParam String token){
        return userService.verifyUser(token);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseBody> authenticateUser(@RequestBody LoginRequestBody body) {
        return userService.authenticate(body.getUsername(), body.getPassword());
    }

    @PostMapping("/auth/disconnect")
    public ResponseEntity<String> disconnect(@RequestParam String token) {
        userService.disconnect(token);
        return ResponseEntity.ok("User Disconnected");
    }

    @GetMapping("/online")
    public ResponseEntity<Object> findConnectedUsers(@RequestParam String token) {
        return userService.findConnectedUsers(token);
    }

    @GetMapping("/details")
    public ResponseEntity<Object> getUserDetails(@RequestParam String token, @RequestParam int id) {
        return userService.getUserDetails(token, id);
    }
}
