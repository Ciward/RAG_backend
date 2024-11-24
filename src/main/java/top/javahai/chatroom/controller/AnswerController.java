package top.javahai.chatroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import top.javahai.chatroom.dao.AnswerDao;
import top.javahai.chatroom.dao.QuestionDao;
import top.javahai.chatroom.entity.Answer;
import top.javahai.chatroom.entity.RespBean;
import top.javahai.chatroom.entity.User;
import top.javahai.chatroom.config.RAGConfig;

@Controller
@RequestMapping("/answer")
public class AnswerController {
  @Autowired
  private AnswerDao answerDao;
  @Autowired
  private QuestionDao questionDao;
  @Autowired
  private RAGConfig ragConfig;

    /**
     * 回答了某一个问题
     * @param authentication
     * @param questionId
     * @param answer
     */
    @RequestMapping("/answerQuestion")
    public RespBean answerQuestion(Authentication authentication, Integer questionId, String answerStr){
      User user = ((User) authentication.getPrincipal());
      Answer answer = new Answer();
      answer.setQuestionId(questionId);
      answer.setContent(answerStr);
      answer.setUserId(user.getId());
      if(answerDao.insert(answer)>=1){
        return RespBean.ok("回答成功！");
      }else{
        return RespBean.error("回答失败！");
      }
    }

    /**
     * 管理员审核回答合法性
     * @param authentication
     * @param answerId
     * @param valid
     */
    @RequestMapping("/checkAnswer")
    public RespBean checkAnswer(Authentication authentication, Integer answerId, Boolean valid){
        User user = ((User) authentication.getPrincipal());
        if(user.getRole().equals("admin")){
          if(answerDao.updateValid(answerId, valid)>=1){
            // 获取问题和回答内容
            Answer answer = answerDao.queryById(answerId);
            String questionContent = questionDao.queryById(answer.getQuestionId()).getContent();
            String answerContent = answer.getContent();

            // 调用 RAGConfig 插入 Q&A
            if (valid) {
                boolean success = ragConfig.insertQA(questionContent, answerContent);
                if (!success) {
                    return RespBean.error("审核成功，但插入Q&A失败！");
                }
            }
            
            return RespBean.ok("审核成功！");
          }else{
            return RespBean.error("审核失败！");
          }
        }else{
          return RespBean.error("您没有权限审核回答！");
        }
    }
}
