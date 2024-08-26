package com.umiverse.umiversebackend.body;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class StorageRequestBody {
    private MultipartFile file;
    private String token;
}
