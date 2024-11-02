package top.javahai.chatroom.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import top.javahai.chatroom.config.GptConfig;

@RestController
@RequestMapping("/api/stream")
public class StreamController {
    @Autowired
    private GptConfig gptConfig;
    @PostMapping("/RAGFileChatStream")
    public SseEmitter streamRAGFileChat(@RequestBody String content, Authentication authentication) {
        // 获取用户信息
        String username = authentication.getName();
        SseEmitter emitter = new SseEmitter();
        
        new Thread(() -> {
            try {
                // 调用GptConfig的RAGFileChat方法
                gptConfig.RAGFileChat(content, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }
    // @PostMapping(value = "/RAGFileChatStreamNoauth", produces = "text/event-stream;charset=UTF-8")
    // public SseEmitter streamRAGFileChatNoauth(@RequestBody Map<String, String> requestBody) {
    //     SseEmitter emitter = new SseEmitter();
    //     // 从请求体中获取content字段
    //     String content = requestBody.get("content");

    //     new Thread(() -> {
    //         try {
    //             // 调用GptConfig的RAGFileChat方法
    //             gptConfig.RAGFileChat(content, emitter);
    //         } catch (Exception e) {
    //             emitter.completeWithError(e);
    //         }
    //     }).start();
    //     return emitter;
    // }
} 