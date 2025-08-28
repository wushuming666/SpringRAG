package org.example.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.ChatEntity;
import org.example.bean.ChatResponseEntity;
import org.example.service.ChatService;
import org.example.utils.SSEServer;
import org.example.enums.SSEMsgType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    private ChatClient chatClient;
    private ChatMemory chatMemory;
    private String systemPrompt =
            """
                你的名字叫Bob
            """;

    //构造器注入
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }

    @Override
    public String chatTest(String message) {
        return chatClient.prompt(message).call().content();
    }
    @Override
    public Flux<String> streamStr(String prompt) {
        return chatClient.prompt(prompt).stream().content();
    }

    @Override
    public void doChat(ChatEntity chatEntity) {

        String userId = chatEntity.getCurrentUserName();
        String prompt = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();

        Flux<String> stringFlux = chatClient.prompt(prompt).stream().content();

        List<String> list = stringFlux.toStream().map(chatResponse -> {
            String content = chatResponse.toString();
            SSEServer.sendMsg(userId, content, SSEMsgType.ADD);
            log.info("content: {}", content);
            return content;
        }).collect(Collectors.toList());

        String fullContent = list.stream().collect(Collectors.joining());

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullContent, botMsgId);

        SSEServer.sendMsg(userId, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.FINISH);

    }
}
