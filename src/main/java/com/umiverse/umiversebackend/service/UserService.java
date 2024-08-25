package com.umiverse.umiversebackend.service;

import com.umiverse.umiversebackend.body.DetailsResponseEntity;
import com.umiverse.umiversebackend.body.RegisterRequestBody;
import com.umiverse.umiversebackend.body.ResponseBody;
import com.umiverse.umiversebackend.model.*;
import com.umiverse.umiversebackend.repository.mysql.UnverifiedUserRepository;
import com.umiverse.umiversebackend.repository.mysql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.umiverse.umiversebackend.exception.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UnverifiedUserRepository unverifiedUserRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private WebSocketService webSocketService;

    public ResponseEntity<ResponseBody> register(RegisterRequestBody body){
        try {
            this.checkUsername(body.getUsername());
            this.checkEmail(body.getEmail());
            this.checkFullName(body.getFullname());
            this.checkRole(body.getRole());
            this.checkPassword(body.getPassword());

            body.setPassword(User.hashPassword(body.getPassword()));

            UnverifiedUser newUser = new UnverifiedUser(body.getUsername(), body.getPassword(), body.getEmail(),
                    body.getFullname(), body.getRole());
            this.saveUnverifiedUser(newUser);
            emailService.sendVerificationEmail(newUser.getEmail(), newUser.getVerificationToken());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseBody("Unverified user registered, verification token sent", newUser.getId()));

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
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseBody(e.getMessage(), 9999)
            );
        }
    }

    public ResponseEntity<ResponseBody> verifyUser(String token){
        try {
            UnverifiedUser user = unverifiedUserRepository.findUnverifiedUsersByVerificationToken(token);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ResponseBody("Invalid verification token", 1008)
                );
            }

            Timestamp expirationDate = user.getTokenExpirationDate();

            if (hasTimestampPassed(expirationDate)) {
                return ResponseEntity.status(HttpStatus.GONE).body(
                        new ResponseBody("Expired verification token", 1009)
                );
            }

            User newUser = new User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole()
            );

            userRepository.save(newUser);

            return ResponseEntity.ok(
                    new ResponseBody("User registered successfully", newUser.getUserID())
            );
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseBody(e.getMessage(), 9999)
            );
        }
    }

    public static boolean hasTimestampPassed(Timestamp timestamp) {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.before(currentTimestamp);
    }


    public ResponseEntity<ResponseBody> authenticate(String username, String password) {
        try {
            String hashedPassword = User.hashPassword(password);
            User user = userRepository.findByUsernameAndPassword(username, hashedPassword);

            if (user != null) {
                user.generateSessionToken();
                saveUser(user);
                webSocketService.sendMessageToTopic("/topic/online", user.getUserID());
                return ResponseEntity.ok(new ResponseBody("User authenticated successfully", user.getSessionToken()));
            } else {
                boolean usernameExists = userRepository.existsByUsername(username);
                if (usernameExists) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ResponseBody("Invalid Password", 2));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ResponseBody("Invalid Username", 1));
                }
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseBody(e.getMessage(), 9999)
            );
        }
    }

    public void saveUser(User user) {
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
//        messagingTemplate.convertAndSend("/topic/online", new UserStatusMessage(user.getUserID(), true));
    }

    public void saveUnverifiedUser(UnverifiedUser user) {
        if(unverifiedUserRepository.existsByUsername(user.getUsername())){
            unverifiedUserRepository.delete(
                    unverifiedUserRepository.getUnverifiedUserByUsername(user.getUsername())
            );
        } else if(unverifiedUserRepository.existsByEmail(user.getEmail())){
            unverifiedUserRepository.delete(
                    unverifiedUserRepository.getUnverifiedUserByEmail(user.getEmail())
            );
        }
        unverifiedUserRepository.save(user);
     }

//    public void disconnect(User user) {
//        User storedUser = userRepository.findByUserID(user.getUserID());
//        if (storedUser != null) {
//            storedUser.setStatus(Status.OFFLINE);
//            userRepository.save(storedUser);
//            messagingTemplate.convertAndSend("/topic/online", new UserStatusMessage(storedUser.getUserID(), false));
//        }
//    }

    public void disconnect(String token) {
        User storedUser = userRepository.findBySessionToken(token);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            storedUser.clearSessionToken();
            userRepository.save(storedUser);
            webSocketService.sendMessageToTopic("/topic/online", storedUser.getUserID());
        }
    }

    public ResponseEntity<Object> findConnectedUsers(String token) {
        if (userRepository.existsBySessionToken(token)) {
            return ResponseEntity.ok(userRepository.findAllByStatus(Status.ONLINE));
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseBody("Invalid user token", 9998));
    }

    public ResponseEntity<Object> getUserDetails(String token, int id) {
        if(!userRepository.existsBySessionToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseBody("Invalid user token", 9998));
        }

        if(!userRepository.existsByUserID(id)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseBody("User not found", 0001));
        }

        User user = userRepository.findById(id).get();
        return ResponseEntity.ok().body(
                new DetailsResponseEntity(user.getUserID(), user.getUsername(), user.getEmail(), user.getFullName(), user.getBio())
        );
    }

    public void checkEmail(String email) throws InvalidEmailException, AlreadyAvailableEmail {
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

    }

    public void checkPassword(String password) throws InvalidPassword {
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

        if(containsUpperCase && containsDigit && isLengthValid) return;
        throw new InvalidPassword();
    }

    public void checkUsername(String username) throws InvalidUsername, AlreadyAvailableUsername {
        boolean existingUser = userRepository.existsByUsername(username);
        System.out.println(existingUser);
        if (existingUser) {
            throw new AlreadyAvailableUsername();
        }

        boolean isLengthValid = username.length() <= 20;
        boolean containsIllegalChars = !username.matches("^[a-zA-Z0-9_.]*$");

        if (!isLengthValid && !containsIllegalChars) throw new InvalidUsername();

    }

    public void checkFullName(String fullName) throws InvalidFullName {
        boolean isLengthValid = fullName.length() <= 30;
        boolean containsOnlyLetters = fullName.matches("^[a-zA-Z ]*$");

        if (!isLengthValid && containsOnlyLetters) throw new InvalidFullName();

    }

    public boolean checkBio(String bio) throws InvalidBio {
        if (!(bio.length() <= 150)) throw new InvalidBio();

        return true;
    }

    public void checkRole(String role) throws InvalidRole {
        if (!(role.equals("student") || role.equals("professor") || role.equals("admin"))) throw new InvalidRole();

    }
}
