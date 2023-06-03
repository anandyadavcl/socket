package com.anand.socket.controller;

import com.anand.socket.service.LogFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;


@EnableScheduling
@Controller
public class SocketController {

    @Autowired
    LogFileReader logFileReader;

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/hello")
    @SendTo("/topic/logs")
    public void greeting(String data) {
        logFileReader.readLastLines(template);
    }

    @Scheduled(fixedRate = 2000)
    public void getData() {
        logFileReader.readFileFromPoint(template);
    }
}
