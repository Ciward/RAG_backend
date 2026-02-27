package top.javahai.chatroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.javahai.chatroom.entity.RespBean;
import top.javahai.chatroom.entity.User;
import top.javahai.chatroom.service.UserService;
@RestController
public class TokenController {

    @Autowired
    private UserService userService;

    /* 检测token是否有效 */
    @GetMapping("/checkToken")
    public RespBean checkToken(Authentication authentication){
        Object principal = authentication.getPrincipal();
        if(principal instanceof User){
            return RespBean.ok("valid");
        }else if(principal instanceof String){
            return RespBean.ok("valid");
        }
        return RespBean.error("invalid");
    }
}

