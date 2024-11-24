package top.javahai.chatroom.entity;

import lombok.Data;

@Data
public class Answer {
    private Integer id;
    private String content;
    private Integer userId;
    private Integer questionId;
    private Boolean valid=false;
    private Data createTime;
}
