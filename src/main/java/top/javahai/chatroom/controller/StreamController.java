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
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                // 获取用户信息
                String username = authentication.getName();
                // 这里可以根据需要使用用户信息进行日志记录或其他操作

                // 调用GptConfig的RAGFileChat方法
                JSONObject response = GptConfig.RAGFileChat(content);

                // 模拟流式发送数据
                if (response != null) {
                    emitter.send(SseEmitter.event().name("message").data(response.toJSONString()));
                }
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }
} 