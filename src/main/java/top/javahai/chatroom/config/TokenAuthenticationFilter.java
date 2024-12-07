package top.javahai.chatroom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.javahai.chatroom.entity.User;
import top.javahai.chatroom.service.impl.UserServiceImpl;
import top.javahai.chatroom.utils.TokenUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private UserServiceImpl userService;

    public TokenAuthenticationFilter() {
        super("/api/**");

    }

    // @Override
    // protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    //         throws ServletException, IOException {
    //     String token = request.getHeader("Authorization");
    //     if (token != null && TokenUtil.verify(token)) {
    //         String userId = TokenUtil.getUserIdFromToken(token);
    //         User user = userService.queryById(Integer.parseInt(userId));
    //         if (user != null) {
    //             TokenAuthentication authentication = new TokenAuthentication(user);
    //             authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    //             SecurityContextHolder.getContext().setAuthentication(authentication);
    //         }
    //     }
    //     chain.doFilter(request, response);
    // }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
            String token = request.getHeader("Authorization");
            if (token != null && TokenUtil.verify(token)) {
                String userId = TokenUtil.getUserIdFromToken(token);
                User user = userService.queryById(Integer.parseInt(userId));
                if (user != null) {
                    TokenAuthentication authentication = new TokenAuthentication(user);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return this.getAuthenticationManager().authenticate(authentication);
                }
            }
            return null;
    }
} 
