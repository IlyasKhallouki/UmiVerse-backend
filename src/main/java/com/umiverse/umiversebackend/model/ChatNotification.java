package com.umiverse.umiversebackend.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "xyz")
public class ChatNotification {
    private String id;
    private String senderId;
    private String recipientId;
    private String content;
}
