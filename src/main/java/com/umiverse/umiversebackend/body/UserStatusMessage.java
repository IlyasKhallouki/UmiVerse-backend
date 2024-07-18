package com.umiverse.umiversebackend.body;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserStatusMessage {
    private int id;
    private boolean online;
}
