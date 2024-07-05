package com.umiverse.umiversebackend.chatroom;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatRoom {
    @Id
    private String id;
    private String chatID;
    private String senderID;
    private String receiverID;
}
