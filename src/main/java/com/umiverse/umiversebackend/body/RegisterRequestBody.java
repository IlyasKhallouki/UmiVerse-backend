package com.umiverse.umiversebackend.body;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestBody {
    String username;
    String fullName;
    String email;
    String password;
    String role;

    public RegisterRequestBody(){}
}
