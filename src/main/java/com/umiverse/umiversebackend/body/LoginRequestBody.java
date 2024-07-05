package com.umiverse.umiversebackend.body;

public class LoginRequestBody {
    private String username;
    private String password;

    public LoginRequestBody(){}

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
