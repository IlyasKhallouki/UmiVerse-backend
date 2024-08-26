package com.umiverse.umiversebackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "bio")
    private String bio;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private Date registrationDate;

    @Column(name = "role")
    private String role;

    @Column(name = "status")
    private Status status;

    @Column(name = "session_token", nullable = true)
    private String sessionToken;

    @Column(name = "token_expiration", nullable = true)
    private Timestamp tokenExpiration;

    @Column(name = "avatar_id")
    private String avatarId;

    public User() {}

    public User(String username, String password, String email, String fullName, String role) {
        this.username = username;
        this.password = hashPassword(password);
        this.email = email;
        this.fullName = fullName;
        this.bio = ":)";
        this.role = role;
        this.registrationDate = getCurrentDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }

    private static Date getCurrentDate() {
        long currentTimeMillis = System.currentTimeMillis();
        return new Date(currentTimeMillis);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashedBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void generateSessionToken() {
        this.sessionToken = hashPassword(this.username + System.currentTimeMillis());
        this.tokenExpiration = new Timestamp(System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000); // 3 days from now
    }

    public void clearSessionToken() {
        this.sessionToken = null;
        this.tokenExpiration = null;
    }
}
