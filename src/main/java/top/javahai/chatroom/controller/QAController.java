package top.javahai.chatroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

import top.javahai.chatroom.dao.AnswerDao;
import top.javahai.chatroom.dao.QuestionDao;
import top.javahai.chatroom.entity.Answer;
import top.javahai.chatroom.entity.RespBean;
import top.javahai.chatroom.entity.User;
import top.javahai.chatroom.config.RAGConfig;
import top.javahai.chatroom.entity.Question;
import top.javahai.chatroom.entity.RespPageBean;

@Controller
@RequestMapping("/QA")
public class QAController {
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
    @PostMapping("/answerQuestion")
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
    @PostMapping("/checkAnswer")
    public RespBean checkAnswer(Authentication authentication, Integer answerId, int valid){
        User user = ((User) authentication.getPrincipal());
        if(user.getRole().equals("admin")){
          if(answerDao.updateValid(answerId, valid, user.getId())>=1){
            // 获取问题和回答内容
            Answer answer = answerDao.queryById(answerId);
            String questionContent = questionDao.queryById(answer.getQuestionId()).getContent();
            String answerContent = answer.getContent();

            // 调用 RAGConfig 插入 Q&A
            if (valid==1) {
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

    /**
     * 分页查询问题
     * @param page
     * @param size
     * @return
     */
    //     调用示例:
    // 10
    // 返回格式将是:
    // {
    //   "total": 100,
    //   "data": [
    //     {
    //       "id": 1,
    //       "content": "问题内容",
    //       "userId": 1,
    //       "createTime": "2024-01-01 12:00:00"
    //     }
    //     // ...更多问题
    //   ]
    // }

    @PostMapping("/getQuestionsByPage")
    public RespPageBean getQuestionsByPage(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        
        List<Question> questions = questionDao.queryByPage(page - 1, size);
        Long total = questionDao.getTotal();
        
        RespPageBean respPageBean = new RespPageBean();
        respPageBean.setData(questions);
        respPageBean.setTotal(total);
        
        return respPageBean;
    }

    /**
     * 根据问题ID查询回答
     * @param questionId
     * @return
     */
    @PostMapping("/getAnswersByQuestionId")
    public RespPageBean getAnswersByQuestionId(Integer questionId){
      List<Answer> answers = answerDao.queryByQuestionId(questionId);
      RespPageBean respPageBean = new RespPageBean();
      respPageBean.setData(answers);
      respPageBean.setTotal(Long.valueOf(answers.size()));
      
      return respPageBean;
    }

    /*
     * 添加问题
     */
    @PostMapping("/addQuestion")
    public RespBean addQuestion(Authentication authentication, String content){
      User user = ((User) authentication.getPrincipal());
      Question question = new Question();
      question.setContent(content);
      question.setUserId(user.getId());
      if(questionDao.insert(question)>=1){
        return RespBean.ok("添加问题成功！");
      }else{
        return RespBean.error("添加问题失败！");
      }
    }
}
