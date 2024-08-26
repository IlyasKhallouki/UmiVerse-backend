package com.umiverse.umiversebackend.controller;

import com.umiverse.umiversebackend.body.ResponseBody;
import com.umiverse.umiversebackend.body.StorageRequestBody;
import com.umiverse.umiversebackend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/storage")
public class StorageController {
    @Autowired
    StorageService storageService;

    @PostMapping("/avatar/add")
    ResponseEntity<ResponseBody> addAvatar(@RequestBody StorageRequestBody body) {
        return storageService.storeAvatar(body.getFile(), body.getToken());
    }

    @GetMapping("/avatar/get")
    ResponseEntity<Object> getAvatar(@RequestParam String token) {
        return storageService.getAvatar(token);
    }

    @GetMapping("/avatar/get-user")
    ResponseEntity<Object> getAvatar(@RequestParam String token, @RequestParam String username) {
        return storageService.getAvatar(token, username);
    }
}
