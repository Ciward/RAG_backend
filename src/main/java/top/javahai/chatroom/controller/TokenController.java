package top.javahai.chatroom.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.javahai.chatroom.entity.RespBean;
import top.javahai.chatroom.entity.User;

@RestController
public class TokenController {

    /* 检测token是否有效 */
    @GetMapping("/checkToken")
    public RespBean checkToken(String token,Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return RespBean.ok("valid");
    }
}

