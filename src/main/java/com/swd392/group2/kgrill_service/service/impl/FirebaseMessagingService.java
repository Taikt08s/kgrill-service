package com.swd392.group2.kgrill_service.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(String token, String title, String body) {
        Message message = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(token)
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (Exception e) {
            System.out.println("Error with firebase messaging:" + e);
        }
    }
}
