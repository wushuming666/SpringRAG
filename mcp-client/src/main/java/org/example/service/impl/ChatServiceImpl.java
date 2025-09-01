package org.example.service.impl;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.ChatEntity;
import org.example.bean.ChatResponseEntity;
import org.example.bean.SearchResult;
import org.example.service.ChatService;
import org.example.service.SesrXngService;
import org.example.utils.SSEServer;
import org.example.enums.SSEMsgType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    private ChatClient chatClient;
    @Resource
    private SesrXngService sesrXngService;
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

    private static final String ragPROMPT = """
                                              基于上下文的知识库内容回答问题：
                                              【上下文】
                                              {context}
                                              
                                              【问题】
                                              {question}
                                              
                                              【输出】
                                              如果没有查到，请回复：不知道。
                                              如果查到，请回复具体的内容。不相关的近似内容不必提到。
                                              """;

    @Override
    public void doChatRagSearch(ChatEntity chatEntity, List<Document> ragContext) {
        String userId = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();

        // 构建提示词
        String context = null;
        if (ragContext != null && ragContext.size() > 0) {
            context = ragContext.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n"));
        }

        // 组装提示词
        Prompt prompt = new Prompt(ragPROMPT
                .replace("{context}", context)
                .replace("{question}", question));

        System.out.println(prompt.toString());

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

    private static final String sesrXngPROMPT = """
                                              你是一个互联网搜索大师，请基于以下互联网返回的结果作为上下文，根据你的理解结合用户的提问综合后，生成并且输出专业的回答：
                                              【上下文】
                                              {context}
                                              
                                              【问题】
                                              {question}
                                              
                                              【输出】
                                              如果没有查到，请回复：不知道。
                                              如果查到，请回复具体的内容。
                                              """;

    @Override
    public void doInternetSearch(ChatEntity chatEntity) {

        String userId = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();

        List<SearchResult> searchResults = sesrXngService.search(question);

        String finalPrompt = buildSesrXngPrompt(question, searchResults);

        // 组装提示词
        Prompt prompt = new Prompt(finalPrompt);

        System.out.println(prompt.toString());

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

    private static String buildSesrXngPrompt(String question, List<SearchResult> searchResults) {

        StringBuilder context = new StringBuilder();

        searchResults.forEach(searchResult -> {
            context.append(
                    String.format("<context>\n[来源] %s \n [摘要] %s \n </context>\n",
                            searchResult.getUrl(),
                            searchResult.getContent()));
        });

        return sesrXngPROMPT
                .replace("{context}", context)
                .replace("{question}", question);
    }
}
