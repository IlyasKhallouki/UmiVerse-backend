package com.umiverse.umiversebackend.service;

import com.umiverse.umiversebackend.body.ResponseBody;
import com.umiverse.umiversebackend.model.User;
import com.umiverse.umiversebackend.repository.mysql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StorageService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${storage.service.url}")
    private String storageServiceUrl;

    public ResponseEntity<ResponseBody> storeAvatar(MultipartFile file, String token) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseBody("Invalid avatar", 2001));
        }

        if (!userRepository.existsBySessionToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseBody("Invalid user", 2002));
        }

        User user = userRepository.findBySessionToken(token);
        String newAvatarId = saveAvatar(file);
        user.setAvatarId(newAvatarId);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseBody("Avatar stored successfully", 1));
    }

    public ResponseEntity<Object> getAvatar(String token) {
        if (!userRepository.existsBySessionToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseBody("Invalid user", 2002));
        }

        User user = userRepository.findBySessionToken(token);
        String avatarId = user.getAvatarId();
        byte[] avatar = requestAvatar(avatarId);
        if (avatar == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseBody("Avatar not found", 2003));
        }
        return ResponseEntity.ok(avatar);
    }

    public ResponseEntity<Object> getAvatar(String token, String username) {
        if (!userRepository.existsBySessionToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseBody("Invalid user", 2002));
        }

        if (!userRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseBody("User does not exist", 2002));
        }

        User user = userRepository.findByUsername(username);
        String avatarId = user.getAvatarId();
        byte[] avatar = requestAvatar(avatarId);
        if (avatar == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseBody("Avatar not found", 2003));
        }
        return ResponseEntity.ok(avatar);
    }

    private String saveAvatar(MultipartFile file) {
        try {
            String uploadUrl = UriComponentsBuilder.fromHttpUrl(storageServiceUrl)
                    .path("/avatar/add")
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultipartFile> requestEntity = new HttpEntity<>(file, headers);

            ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to save avatar: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while saving avatar", e);
        }
    }

    private byte[] requestAvatar(String id) {
        try {
            String downloadUrl = UriComponentsBuilder.fromHttpUrl(storageServiceUrl)
                    .path("/avatar/" + id)
                    .toUriString();

            ResponseEntity<byte[]> response = restTemplate.exchange(downloadUrl, HttpMethod.GET, null, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to retrieve avatar: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving avatar", e);
        }
    }
}
