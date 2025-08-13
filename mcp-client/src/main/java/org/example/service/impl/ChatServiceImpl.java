package org.example.service.impl;

import groovy.util.logging.Slf4j;
import jakarta.annotation.Resource;
import org.example.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
}
