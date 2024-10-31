package top.javahai.chatroom.config;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import top.javahai.chatroom.entity.Question;
import top.javahai.chatroom.utils.HttpRequest;
import top.javahai.chatroom.utils.HttpUtils;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.billing.BillingUsage;
import com.unfbx.chatgpt.entity.billing.CreditGrantsResponse;
import com.unfbx.chatgpt.entity.billing.Subscription;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.completions.Completion;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.sse.ConsoleEventSourceListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 百度文心一言接入
 */
public class GptConfig {
//    public static String getToken(){
//        String token = "";
//        String url = "https://aip.baidubce.com/oauth/2.0/token?client_id=ECU7TJEA7CVK1jL2ES6vzgH8&client_secret=p0W3slm0opKfMWWR09j6px9vj8aY80J5&grant_type=client_credentials";
//        try {
//            token = HttpUtils.httpPost(url);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        JSONObject jsonObject = JSONObject.parseObject(token);
//        String access_token = jsonObject.getString("access_token");
//        return access_token;
//    }
public static String getToken() {
    String token = "";
    String url = "https://aip.baidubce.com/oauth/2.0/token?client_id=ML2S2Kmn7e6rVAE2OBC1g0RG&client_secret=HuYbCQeGtbmcbiPjBflaQOfKuuZH6GiM&grant_type=client_credentials";
    try {
        token = HttpUtils.httpPost(url);
    } catch (Exception e) {
        e.printStackTrace();
    }
    JSONObject jsonObject = JSONObject.parseObject(token);
    if (jsonObject != null && jsonObject.containsKey("access_token")) {
        return jsonObject.getString("access_token");
    } else {
        return null; // 返回空值或者其他默认值，表示获取 token 失败
    }
}
    public static String getMessage(String content) {
        String requestMethod = "POST";
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token="+getToken();//post请求时格式
        HashMap<String, String> msg = new HashMap<>();
        msg.put("role","user");
        msg.put("content",content);
        ArrayList<HashMap> messages = new ArrayList<>();
        messages.add(msg);
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        String outputStr = JSON.toJSONString(requestBody);
        JSON json = HttpRequest.httpRequest(url,requestMethod,outputStr,"application/json");
        JSONObject jsonObject = JSONObject.parseObject(json.toJSONString());
        String result = jsonObject.getString("result");
        System.out.println(result);
        return result;
    }

    public static List<String> preMessage(String content) {
        String requestMethod = "POST";
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token="+getToken();//post请求时格式
        HashMap<String, String> msg = new HashMap<>();
        msg.put("role","user");
        content = "请将下面一句话提取关键信息，将结果以分词的形式展示出来。下面是我的输入文本："+content;
        msg.put("content",content);
        ArrayList<HashMap> messages = new ArrayList<>();
        messages.add(msg);
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        String outputStr = JSON.toJSONString(requestBody);
        JSON json = HttpRequest.httpRequest(url,requestMethod,outputStr,"application/json");
        JSONObject jsonObject = JSONObject.parseObject(json.toJSONString());
        List<String> result = Arrays.asList(jsonObject.getString("result").split(" "));
        System.out.println(result);
        return result;
    }

    public static String resMessage(String question,List<String> content1) {
        String requestMethod = "POST";
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token="+getToken();//post请求时格式
        HashMap<String, String> msg = new HashMap<>();
        msg.put("role","user");
        String content = String.join("\n", content1);//将所有匹配到的信息存入
        //用GPT来进行匹配
        content = "请在下面的数据表中找到最符合问题的答案,如果找不到，那么从网络中搜索答案输出。问题："+question+"\n数据表：\n"+content;
        System.out.println(content);
        msg.put("content",content);
        ArrayList<HashMap> messages = new ArrayList<>();
        messages.add(msg);
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        String outputStr = JSON.toJSONString(requestBody);
        JSON json = HttpRequest.httpRequest(url,requestMethod,outputStr,"application/json");
        JSONObject jsonObject = JSONObject.parseObject(json.toJSONString());
        String result = jsonObject.getString("result");
        System.out.println(result);
        return result;
    }

    public static int RAGchat(Question question) {
       // 返回队列信息
       return 0;
    }
    public static JSONObject requestRAGserver(String content) {
        
        String requestMethod = "POST";
        String url = "http://127.0.0.1:1145/chat/kb_chat";//post请求时格式
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", content);
        requestBody.put("model", "custom-glm4-chat");
        requestBody.put("mode", "local_kb");
        requestBody.put("kb_name", "campus");
        requestBody.put("top_k", 6);
        requestBody.put("score_threshold", 1.0);
        requestBody.put("history", new ArrayList<>());
        requestBody.put("stream", false);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 0);
        requestBody.put("prompt_name", "default");
        requestBody.put("return_direct", false);
        String outputStr = JSON.toJSONString(requestBody);
        JSON json = HttpRequest.httpRequest(url,requestMethod,outputStr,"application/json");
        JSONObject jsonObject = JSONObject.parseObject(json.toJSONString());
        return jsonObject;
    }
//     public static JSONObject openRAGchat(String content) {
        
//         String requestMethod = "POST";
//         String url = "http://127.0.0.1:1145/chat/kb_chat";//post请求时格式
//         OpenAiClient openAiClient = OpenAiClient.builder()
//                 .apiKey(Arrays.asList("EMPTY"))
//                 .apiHost(url)
//                 //自定义key的获取策略：默认KeyRandomStrategy
//                 //.keyStrategy(new KeyRandomStrategy())
//                 //自己做了代理就传代理地址，没有可不不传
// //                .apiHost("https://自己代理的服务器地址/")
//                 .build();
//         Message message = Message.builder().role(Message.Role.USER).content(content).build();
//         ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message)).build();
//         chatCompletion.setStream(true);
//         chatCompletion.setTemperature(0.7);
//         chatCompletion.setModel("custom-glm4-chat");
        
//         ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
//         JSONObject docJsonObject = new JSONObject();
//         docJsonObject.put("content", chatCompletionResponse.getChoices().get(0).getMessage().getContent());
//         chatCompletionResponse.getChoices().forEach(e -> {
//             System.out.println(e.getMessage());
//         });
//         return docJsonObject;
//     }

    /**
     * 将用户的内容发送到QAnything接口，仅获取检索的文档，并返回JSONObject
     *
     * @param content 用户输入的问题内容
     * @return 包含检索文档的JSONObject，如果请求失败，则返回null
     */
    public static JSONObject RAGFileChat(String content) {
        String requestMethod = "POST";
        String url = "http://127.0.0.1:8777/api/local_doc_qa/local_doc_chat"; // 替换{your_host}为实际主机地址

        // 构建请求体
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("kb_ids", Arrays.asList(
            "KB613148ff494c41d4834f13ea66e21a25_240625",
            "KBebb756d6b0a744b29c0044e3773c1aee_240625",
            "KB5739af62043c48f8ab1a782be7a47874_240625"
        )); // 替换为实际的知识库ID
        requestBody.put("question", content);
        requestBody.put("user_id", "zzp");
        requestBody.put("streaming", true); // 是否流式返回
        requestBody.put("history", new ArrayList<>()); // 历史对话
        requestBody.put("networking", false); // 是否开启联网检索
        requestBody.put("product_source", "saas_qa");
        requestBody.put("rerank", true);
        requestBody.put("only_need_search_results", true); // 只需要搜索结果
        requestBody.put("hybrid_search", true);
        requestBody.put("max_token", 70000);
        requestBody.put("api_base", "http://127.0.0.1:9991/v1");
        requestBody.put("api_key", "EMPTY"); // 替换为实际的API密钥
        requestBody.put("model", "custom-glm4-chat");
        requestBody.put("api_context_length", 16384);
        requestBody.put("chunk_size", 2000);
        requestBody.put("top_p", 0.99);
        requestBody.put("temperature", 0.7);

        String outputStr = JSON.toJSONString(requestBody);

        try {
            JSON json = HttpRequest.httpRequest(url, requestMethod, outputStr, "application/json");
            JSONObject jsonObject = JSONObject.parseObject(json.toJSONString());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 请求失败时返回null
        }
    }
}
