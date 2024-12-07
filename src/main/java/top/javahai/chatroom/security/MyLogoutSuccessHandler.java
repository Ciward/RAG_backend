package top.javahai.chatroom.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import top.javahai.chatroom.entity.RespBean;
import top.javahai.chatroom.entity.User;
import top.javahai.chatroom.service.UserService;

@Configuration
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //更新用户状态为离线
        User user = (User) authentication.getPrincipal();
        userService.setUserStateToLeave(user.getId());
        
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out=response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(RespBean.ok("成功退出！")));
        out.flush();
        out.close();
    }
} 