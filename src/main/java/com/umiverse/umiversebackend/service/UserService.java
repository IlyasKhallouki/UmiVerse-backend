package com.umiverse.umiversebackend.service;

import com.umiverse.umiversebackend.body.UserStatusMessage;
import com.umiverse.umiversebackend.model.Status;
import com.umiverse.umiversebackend.model.User;
import com.umiverse.umiversebackend.repository.mysql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.umiverse.umiversebackend.exception.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    public void save(User user) {
        userRepository.save(user);
    }

    public User getUserById(int id) {
        if (userRepository.existsById(id)){
            return userRepository.findById(id).get();
        }
        return null;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User authenticate(String username, String password) {
        messagingTemplate.convertAndSend("/topic/users/updates", "User logged in");
        String hashedPassword = User.hashPassword(password);
        User user = userRepository.findByUsernameAndPassword(username, hashedPassword);

        if(user != null) messagingTemplate.convertAndSend("/topic/online", new UserStatusMessage(user.getUserID(), true));
        return user;
    }

    public void saveUser(User user) {
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
        messagingTemplate.convertAndSend("/topic/online", new UserStatusMessage(user.getUserID(), true));
    }

    public void disconnect(User user) {
        User storedUser = userRepository.findByUserID(user.getUserID());
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            userRepository.save(storedUser);
            messagingTemplate.convertAndSend("/topic/online", new UserStatusMessage(storedUser.getUserID(), false));
        }
    }

    public void disconnect(int id) {
        messagingTemplate.convertAndSend("/topic/users/updates", "User logged out");
        User storedUser = userRepository.findByUserID(id);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            userRepository.save(storedUser);
            messagingTemplate.convertAndSend("/topic/online", new UserStatusMessage(storedUser.getUserID(), false));
        }
    }

    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }

    public boolean checkEmail(String email) throws InvalidEmailException, AlreadyAvailableEmail {
        boolean existingUser = userRepository.existsByEmail(email);
        if (existingUser) {
            throw new AlreadyAvailableEmail();
        }

        String regex1 = "^[a-zA-Z0-9._%+-]+@+(edu\\.umi\\.ac\\.ma)$";
        String regex2 = "^[a-zA-Z0-9._%+-]+@+(umi\\.ac\\.ma)$";

        Pattern pattern1 = Pattern.compile(regex1);
        Pattern pattern2 = Pattern.compile(regex2);
        Matcher matcher1 = pattern1.matcher(email);
        Matcher matcher2 = pattern2.matcher(email);

        boolean isEmailValid = (matcher1.matches() || matcher2.matches()) && email.length() <= 100;

        if(!isEmailValid) throw new InvalidEmailException();

        return true;
    }

    public boolean checkPassword(String password) throws InvalidPassword {
        boolean containsUpperCase = false;
        boolean containsDigit = false;
        boolean isLengthValid = password.length() >= 8;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                containsUpperCase = true;
            } else if (Character.isDigit(ch)) {
                containsDigit = true;
            }
        }

        if(containsUpperCase && containsDigit && isLengthValid) return true;
        throw new InvalidPassword();
    }

    public boolean checkUsername(String username) throws InvalidUsername, AlreadyAvailableUsername {
        boolean existingUser = userRepository.existsByUsername(username);
        System.out.println(existingUser);
        if (existingUser) {
            throw new AlreadyAvailableUsername();
        }

        boolean isLengthValid = username.length() <= 20;
        boolean containsIllegalChars = !username.matches("^[a-zA-Z0-9_.]*$");

        if (!isLengthValid && !containsIllegalChars) throw new InvalidUsername();

        return true;
    }

    public boolean checkFullName(String fullName) throws InvalidFullName {
        boolean isLengthValid = fullName.length() <= 30;
        boolean containsOnlyLetters = fullName.matches("^[a-zA-Z ]*$");

        if (!isLengthValid && containsOnlyLetters) throw new InvalidFullName();

        return true;
    }

    public boolean checkBio(String bio) throws InvalidBio {
        if (!(bio.length() <= 150)) throw new InvalidBio();

        return true;
    }

    public boolean checkRole(String role) throws InvalidRole {
        if (!(role.equals("student") || role.equals("professor") || role.equals("admin"))) throw new InvalidRole();

        return true;
    }
}
