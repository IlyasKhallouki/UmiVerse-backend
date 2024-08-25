package com.umiverse.umiversebackend.body;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBody {
    private String message;
    private int code;
    private String token;

    public ResponseBody(String message, int errorCode) {
        this.message = message;
        this.code = errorCode;
    }

    public ResponseBody(String message, String token) {
        this.message = message;
        this.token = token;
    }
}
