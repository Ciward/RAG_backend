package top.javahai.chatroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;
import top.javahai.chatroom.dao.AnswerDao;
import top.javahai.chatroom.dao.QuestionDao;
import top.javahai.chatroom.entity.Answer;
import top.javahai.chatroom.entity.RespBean;
import top.javahai.chatroom.entity.User;
import top.javahai.chatroom.service.UserService;
import top.javahai.chatroom.config.RAGConfig;
import top.javahai.chatroom.entity.Question;
import top.javahai.chatroom.entity.RespPageBean;

@RestController
@RequestMapping("/QA")
public class QAController {
  @Autowired
  private AnswerDao answerDao;
  @Autowired
  private QuestionDao questionDao;
  @Autowired
  private RAGConfig ragConfig;
  @Autowired
  private UserService userService;

    /**
     * 回答了某一个问题
     * @param authentication
     * @param questionId
     * @param answer
     */
    @PostMapping("/answerQuestion")
    public RespBean answerQuestion(Authentication authentication, @RequestBody Map<String, String> requestBody){
      User user = ((User) authentication.getPrincipal());
      User realUser = userService.loadUserByUsername(user.getUsername());
      Answer answer = new Answer();
      answer.setQuestionId(Integer.parseInt(requestBody.get("questionId")));
      answer.setContent(requestBody.get("answerStr"));
      answer.setUserId(realUser.getId());
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
    public RespBean checkAnswer(Authentication authentication, @RequestBody Map<String, String> requestBody){
      Integer answerId;
      Integer valid;
      try {
        answerId = Integer.parseInt(requestBody.get("answerId"));
        valid = Integer.parseInt(requestBody.get("valid"));
      }catch(NumberFormatException e){
        return RespBean.error("参数格式错误！");
      }
      User user = ((User) authentication.getPrincipal());
      User realUser = userService.loadUserByUsername(user.getUsername());
      if(realUser.getRole().equals("admin")){
        if(answerDao.updateValid(answerId, valid, realUser.getId())>=1){
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
    public RespBean getQuestionsByPage(@RequestBody Map<String, String> requestBody){
      Integer page;
      Integer size;
      try {
        page = Integer.parseInt(requestBody.get("page"));
        size = Integer.parseInt(requestBody.get("size"));
      }catch(NumberFormatException e){
        return RespBean.error("参数格式错误！");
      }
      List<Question> questions = questionDao.queryByPage((page - 1) * size, size);
      
      return RespBean.ok("获取问题成功！", questions);
    }

    /**
     * 根据问题ID查询回答
     * @param questionId
     * @return
     */
    @PostMapping("/getAnswersByQuestionId")
    public RespBean getAnswersByQuestionId(@RequestBody Map<String, String> requestBody){
      Integer questionId;
      try {
        questionId = Integer.parseInt(requestBody.get("questionId"));
      }catch(NumberFormatException e){
        return RespBean.error("参数格式错误！");
      }
      List<Answer> answers = answerDao.queryByQuestionId(questionId);
      
      return RespBean.ok("获取回答成功！", answers);
    }

    /*
     * 添加问题
     */
    @PostMapping("/addQuestion")
    public RespBean addQuestion(Authentication authentication, @RequestBody Map<String, String> requestBody){
      String content = requestBody.get("content");
      if (content == null || content.isEmpty()) {
        return RespBean.error("内容不能为空！");
      }
      User user = ((User) authentication.getPrincipal());
      User realUser = userService.loadUserByUsername(user.getUsername());
      Question question = new Question();
      question.setContent(content);
      question.setUserId(realUser.getId());
      if(questionDao.insert(question) >= 1){
        return RespBean.ok("添加问题成功！");
      } else {
        return RespBean.error("添加问题失败！");
      }
    }
    /*
     * 设置问题为已解决
     */
    @PostMapping("/setQuestionFinished")
    public RespBean setQuestionFinished(@RequestBody Map<String, String> requestBody){
      Integer questionId;
      Boolean finished;
      try {
        questionId = Integer.parseInt(requestBody.get("questionId"));
        finished = Boolean.parseBoolean(requestBody.get("finished"));
      }catch(NumberFormatException e){
        return RespBean.error("参数格式错误！");
      }
      Question question = questionDao.queryById(questionId);
      question.setFinished(finished ? 1 : 0);
      if(questionDao.update(question)>=1){
          return RespBean.ok("设置问题为已解决！");
        }else{
          return RespBean.error("设置问题为已解决失败！");
        }
    }
}
