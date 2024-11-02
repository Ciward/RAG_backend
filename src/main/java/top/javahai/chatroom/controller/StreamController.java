package top.javahai.chatroom.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson.JSONObject;

import top.javahai.chatroom.config.GptConfig;

import java.io.IOException;

@RestController
@RequestMapping("/api/stream")
public class StreamController {

    @PostMapping("/RAGFileChatStream")
    public SseEmitter streamRAGFileChat(@RequestBody String content, Authentication authentication) {
        // 获取用户信息
        String username = authentication.getName();
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                // 这里可以根据需要使用用户信息进行日志记录或其他操作
                // 调用GptConfig的RAGFileChat方法
                GptConfig.RAGFileChat(content, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }
    @PostMapping("/RAGFileChatStreamNoauth")
    public SseEmitter streamRAGFileChatNoauth(@RequestBody String content) {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                // 调用GptConfig的RAGFileChat方法
                GptConfig.RAGFileChat(content, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }
} 