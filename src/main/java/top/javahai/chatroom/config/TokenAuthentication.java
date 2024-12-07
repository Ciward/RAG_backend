package top.javahai.chatroom.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import top.javahai.chatroom.entity.User;
public class TokenAuthentication extends AbstractAuthenticationToken {
    private final User user;

    public TokenAuthentication(User user) {
        super(user.getAuthorities());
        this.user = user;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public User getPrincipal() {
        return user;
    }
}
