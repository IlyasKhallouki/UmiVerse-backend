package com.umiverse.umiversebackend.repository;

import com.umiverse.umiversebackend.chatroom.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom,String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderID, String recipientID);
}
