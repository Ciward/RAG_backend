package top.javahai.chatroom.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.binarywang.java.emoji.EmojiConverter;

import top.javahai.chatroom.config.GptConfig;
import top.javahai.chatroom.dao.QuestionDao;
import top.javahai.chatroom.entity.GroupMsgContent;
import top.javahai.chatroom.entity.Message;
import top.javahai.chatroom.entity.Notice;
import top.javahai.chatroom.entity.Question;
import top.javahai.chatroom.entity.User;
import top.javahai.chatroom.service.GroupMsgContentService;
import top.javahai.chatroom.service.NoticeService;
import top.javahai.chatroom.utils.HttpUtils;

/**
 */
@Controller
public class WsController {
  @Autowired
  SimpMessagingTemplate simpMessagingTemplate;


  @Autowired
  private QuestionDao questionDao;

  

  @Autowired
  private NoticeService noticeService;
  /**
   * 单聊的消息的接受与转发
   * @param authentication
   * @param message
   */
  @MessageMapping("/ws/chat")
  public void handleMessage(Authentication authentication, Message message){
    User user= ((User) authentication.getPrincipal());
    message.setFromNickname(user.getNickname());
    message.setFrom(user.getUsername());
    message.setCreateTime(new Date());
    simpMessagingTemplate.convertAndSendToUser(message.getTo(),"/queue/chat",message);
  }

  @Autowired
  GroupMsgContentService groupMsgContentService;
  EmojiConverter emojiConverter = EmojiConverter.getInstance();

  SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  /**
   * 群聊的消息接受与转发
   * @param authentication
   * @param groupMsgContent
   */
  @MessageMapping("/ws/groupChat")
  public void handleGroupMessage(Authentication authentication, GroupMsgContent groupMsgContent){
    User currentUser= (User) authentication.getPrincipal();
    //处理emoji内容,转换成unicode编码
    groupMsgContent.setContent(emojiConverter.toHtml(groupMsgContent.getContent()));
    //保证来源正确性，从Security中获取用户信息
    groupMsgContent.setFromId(currentUser.getId());
    groupMsgContent.setFromName(currentUser.getNickname());
    groupMsgContent.setFromProfile(currentUser.getUserProfile());
    groupMsgContent.setCreateTime(new Date());
    //保存该条群聊消息记录到数据库中
    groupMsgContentService.insert(groupMsgContent);
    //转发该条数据
    simpMessagingTemplate.convertAndSend("/topic/greetings",groupMsgContent);
  }


    /**
     * 接受前端发来的消息，获得图灵机器人回复并转发回给发送者
     * @param authentication
     * @param message
     * @throws IOException
     */
    @MessageMapping("/ws/robotChat")
    public void handleRobotChatMessage(Authentication authentication, Message message) throws IOException {
      User user = ((User) authentication.getPrincipal());
      //接收到的消息
      message.setFrom(user.getUsername());
      message.setCreateTime(new Date());
      message.setFromNickname(user.getNickname());
      message.setFromUserProfile(user.getUserProfile());

      //先拿整个输入信息去数据库查，查不到调用文心一言来进行文本预处理，如果还查不到，则去问gpt
      QueryWrapper<Notice> wrapper = new QueryWrapper<>();
      wrapper.eq("title",message.getContent());  //整个信息去数据库中查询
      List<Notice> list = noticeService.list(wrapper);  //使用服务层的list方法根据构建的条件查询通知，并将结果存储在list中。
      Message resultMessage = new Message();           //创建一个新的Message对象，并设置其一些基本属性，比如发送者（小智）、创建时间（当前时间）以及发送者昵称（也是小智）。
      resultMessage.setFrom("小智");
      resultMessage.setCreateTime(new Date());
      resultMessage.setFromNickname("小智");
      //接下来是一个条件语句，根据之前查询结果的不同情况执行不同的逻辑
      if(list.size() == 0){
        List<String> words = GptConfig.preMessage(message.getContent());//使用GPT预处理工具提取关键词，并进一步搜索。
        if(words.size() == 0){//如果分词没有结果，用GPT网络搜索并进行回答
          String result = GptConfig.getMessage(message.getContent());
          resultMessage.setContent(result+"\r\n答案来源：文心一言");
        }else {//如果搜索到了关键词，则在本地数据库中搜索
          QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
          for (String word: words) {
            // 每个词都作为一个条件，使用 "或" 连接
            queryWrapper.or().like("title", "%" + word + "%");
          }
          List<Notice> list1 = noticeService.list(queryWrapper);//使用更新后的查询条件，再次通过noticeService.list查询通知。
          if(list1.size() == 0){    //如果再次查询结果为空，则同样调用GptConfig.getMessage获取回复。
            // 调用gpt智能回答
            String result = GptConfig.getMessage(message.getContent());
            resultMessage.setContent(result+"\r\n回答来源：文心一言");
          }else{//如果list1中有东西
            // 遍历list1，提取标题并存入list2
            List<String> list2 = new ArrayList<>();
            for (Notice notice : list1) {
              if(notice.getId()>1000000){
                list2.add(notice.getTitle()+"url:"+notice.getUrl());
              }else
              list2.add(notice.getTitle());
            }
            String result = GptConfig.resMessage(message.getContent(),list2);
            resultMessage.setContent(result+"\r\n回答来源：本地数据库");
//            resultMessage.setContent(list1.get(0).getTitle()+"\r\n回答来源："+list1.get(0).getUrl());//如果查询到通知，则取第一个通知的标题和URL作为回复内容。
          }
        }
      }else {
        resultMessage.setContent(list.get(0).getTitle()+"\r\n回答来源："+list.get(0).getUrl());//如果一开始list不为空，则直接取第一个通知的标题和URL作为回复内容。
        System.out.println(resultMessage.getContent());//将resultMessage的内容输出到控制台，这通常用于调试。
      }
      //回送机器人回复的消息给发送者
      simpMessagingTemplate.convertAndSendToUser(message.getFrom(),"/queue/robot",resultMessage);
    }


    /**
     * 接受前端发来的消息，获得RAG后端的回复并转发回给发送者
     * @param authentication
     * @param message
     * @throws IOException
     */
    @RequestMapping("/ws/RAGChat")
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

       /**
     * 接受前端发来的消息，调用python接口并转发回给发送者
     * @param authentication
     * @param message
     * @throws IOException
     */
    @MessageMapping("/ws/fileChat")
    public void handleFileChatMessage(Authentication authentication, Message message) throws IOException {
      User user = ((User) authentication.getPrincipal());
      //接收到的消息
      message.setFrom(user.getUsername());
      message.setCreateTime(new Date());
      message.setFromNickname(user.getNickname());
      message.setFromUserProfile(user.getUserProfile());
      //questionDao.insert(message);

      try {
        // 构建请求体
        String queryParam = message.getContent();
        String encodedQuery = URLEncoder.encode(queryParam,"UTF-8");
        String postResponse = HttpUtils.httpPost("http://localhost:1145/query?query=" + encodedQuery);
        System.out.println("POST Response: " + postResponse);
        // 解析JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(postResponse);

        // 处理JSON数据
        System.out.println("query: " + jsonNode.get("query").asText());
        JsonNode referenceNode = jsonNode.get("reference");
        List<JsonNode> refList = new ArrayList<>();
        List<String> texList = new ArrayList<>();
        List<String> fileList = new ArrayList<>();
        if (referenceNode.isArray()) {
            for (JsonNode node : referenceNode) {
                refList.add(node);
                texList.add(node.get("text").asText());
                fileList.add(node.get("file_name").asText());
            }
        }
      
        Message resultMessage = new Message();           //创建一个新的Message对象，并设置其一些基本属性，比如发送者（小智）、创建时间（当前时间）以及发送者昵称（也是小智）。
        resultMessage.setFrom("小智");
        resultMessage.setCreateTime(new Date());
        resultMessage.setFromNickname("小智");
        
        String tex = String.join("", texList);  // 使用空字符串作为分隔符
        String files = String.join("", fileList);  // 使用空字符串作为分隔符
        resultMessage.setContent(tex+'\n'+"来源:"+files);

        // //回送机器人回复的消息给发送者
        simpMessagingTemplate.convertAndSendToUser(message.getFrom(),"/queue/robot",resultMessage);
    
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
}
