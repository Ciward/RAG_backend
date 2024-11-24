package top.javahai.chatroom.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import top.javahai.chatroom.dao.QuestionDao;
import top.javahai.chatroom.entity.Message;
import top.javahai.chatroom.entity.Question;
import top.javahai.chatroom.entity.User;


/**
 */
@Controller
@RequestMapping("/rag")
public class RAGController {

  @Autowired
  private QuestionDao questionDao;

    /**
     * 接受前端发来的消息，获得RAG后端的回复并转发回给发送者
     * @param authentication
     * @param message
     * @throws IOException
     */
    @RequestMapping("/RAGChat")
    public Map<String,Object> handleRAGChatMessage(Authentication authentication, Message message) throws IOException {
      User user = ((User) authentication.getPrincipal());
      //接收到的消息
      message.setFrom(user.getUsername());
      message.setCreateTime(new Date());
      message.setFromNickname(user.getNickname());
      message.setFromUserProfile(user.getUserProfile());
      Question question = new Question();
      question.setContent(message.getContent());
      question.setCreateTime(new Date());
      question.setUserId(user.getId());
      questionDao.insert(question);
      
      try {
        // 认证成功,返回可以请求
        Map<String,Object> map = new HashMap<>();
        map.put("code",200);
        map.put("message","success");
        return map;
      } catch (Exception e) {
          e.printStackTrace();
          Map<String,Object> map = new HashMap<>();
          map.put("code",500);
          map.put("message","error");
          return map;
      }
    }

}
