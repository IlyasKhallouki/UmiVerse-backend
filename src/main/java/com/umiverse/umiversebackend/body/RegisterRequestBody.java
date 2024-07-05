package com.umiverse.umiversebackend.body;

public class RegisterRequestBody {
    String username;
    String fullName;
    String email;
    String password;
    String role;

    public RegisterRequestBody(){}

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
