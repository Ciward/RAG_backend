package top.javahai.chatroom.config;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import top.javahai.chatroom.security.JwtAuthenticatioToken;
import top.javahai.chatroom.security.JwtAuthenticationFilter;
import top.javahai.chatroom.security.JwtAuthenticationProvider;
import top.javahai.chatroom.security.JwtLoginFilter;
import top.javahai.chatroom.security.MyAuthenticationFailureHandler;
import top.javahai.chatroom.security.MyAuthenticationSuccessHandler;
import top.javahai.chatroom.security.MyLogoutSuccessHandler;
import top.javahai.chatroom.utils.JwtTokenUtils;
import top.javahai.chatroom.config.VerificationCodeFilter;
import top.javahai.chatroom.entity.RespBean;
import top.javahai.chatroom.entity.User;
import top.javahai.chatroom.service.UserService;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;
    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;
    @Autowired
    private VerificationCodeFilter verificationCodeFilter;
    // @Autowired
    // private UserService userService;
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定义登录身份认证组件
        auth.authenticationProvider(new JwtAuthenticationProvider(userDetailsService));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用 csrf, 由于使用的是JWT，我们这里不需要csrf
        http.cors().and().csrf().disable()
            .authorizeRequests()
            // 跨域预检请求
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // swagger
            // .antMatchers("/swagger**/**").permitAll()
            // .antMatchers("/webjars/**").permitAll()
            // .antMatchers("/v2/**").permitAll()
            .antMatchers("/login","/verifyCode","/file","/ossFileUpload","/user/register","/user/checkUsername","/user/checkNickname","/api/stream/RAGFileChatStreamNoauth").permitAll()
            // 其他所有请求需要身份认证
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .usernameParameter("username")
            .passwordParameter("password")
            .loginPage("/login")
            .loginProcessingUrl("/doLogin")
            //成功处理
            .successHandler(myAuthenticationSuccessHandler)
            //失败处理
            .failureHandler(myAuthenticationFailureHandler)
            .permitAll()//返回值直接返回
            .and()
            //登出处理
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler(myLogoutSuccessHandler)
            .permitAll();
        // 开启登录认证流程过滤器
        // http.addFilterBefore(new JwtLoginFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(verificationCodeFilter, UsernamePasswordAuthenticationFilter.class);
        // 访问控制时登录状态检查过滤器
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
    
}