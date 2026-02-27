package top.javahai.chatroom.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Question {
    private Integer id;
    private String content;
    private Integer userId;
    private Date createTime;
    private int finished;
}
