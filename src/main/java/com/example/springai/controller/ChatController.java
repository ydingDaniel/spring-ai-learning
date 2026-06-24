package com.example.springai.controller;

import com.example.springai.tools.CommonTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final CommonTools commonTools;

    public ChatController(ChatClient.Builder builder, CommonTools commonTools) {
        this.commonTools = commonTools;
        this.chatClient = builder
                .defaultSystem("你是一个友好的 AI 助手，请用中文回答问题。")
                .build();
    }

    /**
     * 普通对话
     * GET /ai/chat?message=你好
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 带工具的对话 — AI 可自主调用工具完成任务
     * GET /ai/agent?message=北京今天天气怎么样？另外3乘以7等于多少？
     */
    @GetMapping("/agent")
    public String agent(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .tools(commonTools)   // 注册工具
                .call()
                .content();
    }

    /**
     * 流式对话（SSE）
     * GET /ai/stream?message=介绍一下Spring AI
     */
    @GetMapping("/stream")
    public Flux<String> stream(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
