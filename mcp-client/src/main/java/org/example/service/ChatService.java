package org.example.service;
import org.example.bean.ChatEntity;
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

    /**
     * 和大模型交互
     * @param
     * @return
     */
    public void doChat(ChatEntity chatEntity);
}
