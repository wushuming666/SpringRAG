package org.example.controller;

import jakarta.annotation.Resource;
import org.example.bean.ChatEntity;
import org.example.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chat")
public class ChatController {
    @Resource
    private ChatService chatService;

    @PostMapping("doChat")
    public void doChat(@RequestBody ChatEntity chatEntity){
        chatService.doChat(chatEntity);
    }

}
