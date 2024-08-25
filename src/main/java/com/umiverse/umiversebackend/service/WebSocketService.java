package com.umiverse.umiversebackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendMessageToTopic(String topic, Object message) {
        messagingTemplate.convertAndSend(topic, message);
    }

    public void sendMessageToUser(String username, Object message) {
        messagingTemplate.convertAndSend("/user/" + username + "/queue/notifications", message);
    }
}
