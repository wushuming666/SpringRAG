package org.example.enums;

public enum SSEMsgType {
    MESSAGE("message","单次发送的普通类型消息"),
    ADD("add","消息追加，适用于流式stream推送"),
    CUSTOM_EVENT("custom_event","单次发送的普通类型消息"),
    FINISH("finish","消息完成"),
    DONE("done", "消息done");

    public final String type;
    public final String value;

    SSEMsgType(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
