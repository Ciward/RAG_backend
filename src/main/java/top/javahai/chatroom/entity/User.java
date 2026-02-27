package top.javahai.chatroom.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Collections;



/**
 * (User)实体类
 */

public class User implements UserDetails {

    // public User(String username, String password, Collection<? extends GrantedAuthority> authorities){
    //     this.username=username;
    //     this.password=password;

    // }
    // public User(JwtUserDetails jwtUserDetails){
    //     this.username=jwtUserDetails.getUsername();
    //     this.password=jwtUserDetails.getPassword();
    // }
    /**
     * 用户id
     */
    private Integer id;
    /**
     * 登录账号
     */
    private String role="user";
    /**
     * 用户角色
     */
    private String username;

    /**
     * 密码
     */
    private String password;
    /**
     * 用户头像
     */
    private String userProfile;
    /**
     * 用户状态id
     */
    private Integer userStateId;
    /**
     * 是否可用
     */
    private Boolean isEnabled;
    /**
     * 是否被锁定
     */
    private Boolean isLocked;
    /**
     * 性别
     */
    private String gender;
    /**
     * 姓名
     */
    private String name;
    /**
     * 学号
     */
    private String studentId;
    private String nation;
    private String hometown;

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    //账号是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //账号是否不锁定
    @Override
    public boolean isAccountNonLocked() {
        return  !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return  isEnabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取用户拥有的所有角色
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public Integer getUserStateId() {
        return userStateId;
    }

    public void setUserStateId(Integer userStateId) {
        this.userStateId = userStateId;
    }


    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userProfile='" + userProfile + '\'' +
                ", userStateId=" + userStateId +
                ", isEnabled=" + isEnabled +
                ", isLocked=" + isLocked +
                ", gender='" + gender + '\'' +
                ", name='" + name + '\'' +
                ", studentId='" + studentId + '\'' +
                ", nation='" + nation + '\'' +
                ", hometown='" + hometown + '\'' +
                '}';
    }
}
