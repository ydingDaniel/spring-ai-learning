package com.example.springai.controller;

import com.example.springai.tools.CommonTools;
import com.example.springai.tools.ShoppingTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/memory")
public class MemoryController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public MemoryController(ChatClient.Builder builder,
                            CommonTools commonTools,
                            ShoppingTools shoppingTools) {
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();

        this.chatClient = builder
                .defaultSystem("你是一个购物助手，记住用户的问题，回答时结合上下文。")
                .defaultTools(commonTools, shoppingTools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @GetMapping
    public String chat(@RequestParam String message,
                       @RequestParam(defaultValue = "default") String sessionId) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .content();
    }

    @GetMapping("/debug")
    public String debug(@RequestParam(defaultValue = "default") String sessionId) {
        List<Message> messages = chatMemory.get(sessionId);
        StringBuilder sb = new StringBuilder();
        sb.append("=== 发给模型的消息结构 ===\n\n");
        sb.append("[0] SYSTEM: \"你是一个购物助手，记住用户的问题，回答时结合上下文。\"\n\n");

        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            String content = msg.getText();
            if (content != null && content.length() > 100) {
                content = content.substring(0, 100) + "...";
            }
            sb.append(String.format("[%d] %s: \"%s\"\n\n",
                    i + 1, msg.getMessageType(), content));
        }
        sb.append("[最后] USER: \"<本次用户输入>\"\n");
        return sb.toString();
    }

    @GetMapping("/clear")
    public String clear(@RequestParam(defaultValue = "default") String sessionId) {
        chatMemory.clear(sessionId);
        return "会话 [" + sessionId + "] 历史已清空";
    }
}
