package com.umiverse.umiversebackend.service;

import com.umiverse.umiversebackend.model.User;
import com.umiverse.umiversebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User authenticate(String username, String password) {
        String hashedPassword = User.hashPassword(password);
        return userRepository.findByUsernameAndPassword(username, hashedPassword);
    }
}
