package com.umiverse.umiversebackend.service;

import com.umiverse.umiversebackend.chatroom.ChatRoom;
import com.umiverse.umiversebackend.repository.mongodb.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(
            String senderID,
            String recipientID,
            boolean createNewRoomIfNotExists
    ){
        return chatRoomRepository.findBySenderIdAndRecipientId(senderID, recipientID)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                        try {
                            var chatID = createChat(senderID, recipientID);
                            return Optional.of(chatID);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return Optional.empty();
                });
    }

    public String createChat(String str1, String str2) throws NoSuchAlgorithmException {
        String combinedString = str1 + str2;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(combinedString.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String chatID = hexString.toString();

        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatID)
                .senderId(str1)
                .recipientId(str2)
                .build();

        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatID)
                .senderId(str2)
                .recipientId(str1)
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatID;
    }
}
