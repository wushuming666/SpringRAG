package org.example.service;
import reactor.core.publisher.Flux;

public interface ChatService {
    /**
     * 测试大模型聊天接口
     * @param message
     * @return
     */
    public String chatTest(String message);

    /**
     * 测试大模型流式接口
     * @param prompt
     * @return
     */
    public Flux<String> streamStr(String prompt);
}
