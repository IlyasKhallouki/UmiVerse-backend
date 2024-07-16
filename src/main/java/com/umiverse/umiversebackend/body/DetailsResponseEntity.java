package com.umiverse.umiversebackend.body;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DetailsResponseEntity {
    private int id;
    private String username;
    private String email;
    private String fullname;
    private String bio;
}
