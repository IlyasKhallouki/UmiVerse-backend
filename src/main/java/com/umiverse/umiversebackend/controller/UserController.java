package com.umiverse.umiversebackend.controller;

import com.umiverse.umiversebackend.body.*;
import com.umiverse.umiversebackend.body.ResponseBody;
import com.umiverse.umiversebackend.exception.*;
import com.umiverse.umiversebackend.model.User;
import com.umiverse.umiversebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseBody> registerUser(@RequestBody RegisterRequestBody body) {
        try {
            userService.checkUsername(body.getUsername());
            userService.checkEmail(body.getEmail());
            userService.checkFullName(body.getFullName());
            userService.checkRole(body.getRole());
            userService.checkPassword(body.getPassword());

            body.setPassword(User.hashPassword(body.getPassword()));

            User newUser = new User(body.getUsername(), body.getPassword(), body.getEmail(),
                    body.getFullName(), body.getRole());
            userService.save(newUser);
            userService.saveUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseBody("User registered successfully", 0));

        } catch (AlreadyAvailableUsername e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseBody("Username already exists", 1001));
        } catch (InvalidUsername e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseBody("Invalid username", 1002));
        } catch (InvalidPassword e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseBody("Invalid password", 1003));
        } catch (AlreadyAvailableEmail e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseBody("Email already exists", 1004));
        } catch (InvalidEmailException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseBody("Invalid email", 1005));
        } catch (InvalidFullName e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseBody("Invalid full name", 1006));
        } catch (InvalidRole e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseBody("Invalid role", 1007));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginRequestBody body) {
        User authenticatedUser = userService.authenticate(body.getUsername(), body.getPassword());
        if (authenticatedUser != null) {
            userService.saveUser(authenticatedUser);
            return ResponseEntity.ok("User authenticated successfully");
        } else {
            boolean usernameExists = userService.existsByUsername(body.getUsername());
            if (usernameExists) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username does not exist");
            }
        }
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
}
