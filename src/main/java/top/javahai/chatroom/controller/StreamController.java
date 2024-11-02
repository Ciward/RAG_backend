package top.javahai.chatroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import top.javahai.chatroom.config.GptConfig;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.PostConstruct;

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