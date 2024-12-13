package top.javahai.chatroom.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.javahai.chatroom.entity.RespBean;
import top.javahai.chatroom.entity.User;
import top.javahai.chatroom.service.UserService;
import top.javahai.chatroom.utils.JwtTokenUtils;
@Configuration
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private UserService userService;
    // @Autowired
    // private AuthenticationManager authenticationManager;
    private Logger logger = LoggerFactory.getLogger(MyLogoutSuccessHandler.class);
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //更新用户状态为离线
        User user = (User) authentication.getPrincipal();
        userService.setUserStateToLeave(user.getId());
        // 让 session 失效　
        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.debug("Invalidating session: " + session.getId());
            session.invalidate();
        }
        // 清理 Security 上下文，其中包含登录认证信息
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);

        SecurityContextHolder.clearContext();
        // 手动使令牌失效
        JwtTokenUtils.invalidateToken(request.getHeader("Authorization"));
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out=response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(RespBean.ok("成功退出！")));
        out.flush();
        out.close();
    }
} 