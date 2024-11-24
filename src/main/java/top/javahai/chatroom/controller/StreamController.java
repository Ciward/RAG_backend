package top.javahai.chatroom.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import top.javahai.chatroom.config.RAGConfig;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/stream")
public class StreamController {
    @Autowired
    private RAGConfig RAGConfig;
    @PostMapping("/RAGFileChatStream")
    public SseEmitter streamRAGFileChat(@RequestBody String content, Authentication authentication) {
        // 获取用户信息
        String username = authentication.getName();
        SseEmitter emitter = new SseEmitter();
        
        new Thread(() -> {
            try {
                // 调用RAGConfig的RAGFileChat方法
                RAGConfig.RAGFileChat(content, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }

    @PostMapping(value = "/RAGFileChatStreamNoauth")
    public SseEmitter streamRAGFileChatNoauth(@RequestBody Map<String, String> requestBody) {
        SseEmitter emitter = new SseEmitter();
        // 从请求体中获取content字段
        String content = requestBody.get("content");

        new Thread(() -> {
            try {
                // 调用RAGConfig的RAGFileChat方法
                RAGConfig.RAGFileChat(content, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }
} 