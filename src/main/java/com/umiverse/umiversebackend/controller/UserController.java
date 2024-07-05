package com.umiverse.umiversebackend.controller;

import com.umiverse.umiversebackend.body.LoginRequestBody;
import com.umiverse.umiversebackend.model.User;
import com.umiverse.umiversebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        if (userService.existsByUsername(newUser.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        if (userService.existsByEmail(newUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        newUser.setPassword(User.hashPassword(newUser.getPassword()));

        userService.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginRequestBody body) {
        User authenticatedUser = userService.authenticate(body.getUsername(), body.getPassword());
        if (authenticatedUser != null) {
            return ResponseEntity.ok("User authenticated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
