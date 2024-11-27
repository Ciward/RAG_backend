package top.javahai.chatroom.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Answer {
    private Integer id;
    private String content;
    private Integer userId;
    private Integer questionId;
    private int valid=0;
    private Date createTime;
}
