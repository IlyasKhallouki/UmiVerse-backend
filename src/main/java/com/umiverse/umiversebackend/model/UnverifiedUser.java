package com.umiverse.umiversebackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "unverified_users")
public class UnverifiedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "bio")
    private String bio;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "role")
    private String role;

    @Column(name = "token_expiry_date")
    private Timestamp tokenExpirationDate;

    @Column(name = "verification_token")
    private String verificationToken;


    public UnverifiedUser() {}

    public UnverifiedUser(String username, String password, String email, String fullName, String role){
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.bio = ":)";
        this.role = role;
        this.registrationDate = getCurrentDate();
        this.tokenExpirationDate = calculateTokenExpiryDate();
        this.verificationToken = generateVerificationToken(); //
    }

    // get the current date when registering
    private static Date getCurrentDate() {
        long currentTimeMillis = System.currentTimeMillis();

        return new Date(currentTimeMillis);
    }

    // get the token expiration date by adding 30 minutes to the current timestamp
    private static Timestamp calculateTokenExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        return new Timestamp(calendar.getTimeInMillis());
    }

    // generate a unique verification token
    public static String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
}

