package com.umiverse.umiversebackend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "xyz")
public class ChatMessage {
    @Id
    private String id;
    private String senderId;
    private String recipientId;
    private String chatId;
    private String content;
}

