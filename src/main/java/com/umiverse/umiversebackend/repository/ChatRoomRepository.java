package com.umiverse.umiversebackend.repository;

import com.umiverse.umiversebackend.chatroom.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom,String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderID, String recipientID);
}
