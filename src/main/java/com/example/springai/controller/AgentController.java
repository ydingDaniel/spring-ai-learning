package com.example.springai.controller;

import com.example.springai.tools.CommonTools;
import com.example.springai.tools.ShoppingTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/react")
public class AgentController {

    private final ChatClient chatClient;

    public AgentController(ChatClient.Builder builder,
                           CommonTools commonTools,
                           ShoppingTools shoppingTools) {
        this.chatClient = builder
                .defaultSystem("""
                        你是一个购物助手，帮助用户查询商品信息。
                        当用户询问商品时，请按顺序：
                        1. 先查询原价
                        2. 再查询折后价（需要用原价作为参数）
                        3. 最后查询库存
                        根据以上信息给出购买建议。
                        """)
                .defaultTools(commonTools, shoppingTools)
                .build();
    }

    /**
     * ReAct Agent：多步推理，每步工具调用依赖上一步结果
     * GET /ai/react?message=我想买 iPhone 15，帮我查一下价格、折扣和库存
     */
    @GetMapping
    public String react(@RequestParam String message) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();

        // 打印每一步的工具调用（在 IDEA 控制台可以看到）
        for (Generation generation : response.getResults()) {
            AssistantMessage msg = generation.getOutput();
            if (msg.getToolCalls() != null && !msg.getToolCalls().isEmpty()) {
                msg.getToolCalls().forEach(call ->
                        System.out.printf("[ReAct] 调用工具: %s，参数: %s%n",
                                call.name(), call.arguments()));
            }
        }

        return response.getResult().getOutput().getText();
    }
}
