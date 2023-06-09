package com.anand.socket.controller;

import com.anand.socket.dto.Message;
import com.anand.socket.service.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static com.anand.socket.constants.Constants.FILEPATH;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/private-message")
    public void getPrivateMessage(final Message message,
                                  final Principal principal) throws InterruptedException {
        Thread.sleep(1000);
        FileReader reader = new FileReader(FILEPATH,
                true, messagingTemplate, principal.getName());
        Thread thread = new Thread(reader);
        thread.start();
    }
}
