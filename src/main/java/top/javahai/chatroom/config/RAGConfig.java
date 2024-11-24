package top.javahai.chatroom.config;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.alibaba.fastjson.JSON;

/**
 * 访问QAnything接口
 */
@Component
public class RAGConfig {

    @Value("${app.server.ip}")
    private String serverIp;
    @Value("${app.server.port}")
    private String serverPort;

    /**
     * 将用户的内容发送到QAnything接口，返回JSONObject
     *
     * @param content 用户输入的问题内容
     * @return 包含检索文档的JSONObject，如果请求失败，则返回null
     */
    public void RAGFileChat(String content, SseEmitter emitter) {
        String requestMethod = "POST";
        String url = "http://" + serverIp + ":" + serverPort + "/api/local_doc_qa/local_doc_chat";

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
        requestBody.put("product_source", "saas");
        requestBody.put("rerank", true);
        requestBody.put("only_need_search_results", false); // 只需要搜索结果
        requestBody.put("hybrid_search", true);
        requestBody.put("max_token", 7114);
        requestBody.put("api_base", "http://" + serverIp + ":9997/v1");
        requestBody.put("api_key", "EMPTY"); // 替换为实际的API密钥
        requestBody.put("model", "custom-glm4-chat");
        requestBody.put("api_context_length", 72704);
        requestBody.put("chunk_size", 2000);
        requestBody.put("top_p", 0.9);
        requestBody.put("top_k", 40);
        requestBody.put("temperature", 0.7);

        String outputStr = JSON.toJSONString(requestBody);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = outputStr.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    // 修改返回格式
                    if (responseLine.startsWith("data: ")) {
                        responseLine = responseLine.replaceFirst("data: ", "");
                    }
                    if(!responseLine.equals(""))
                        emitter.send(SseEmitter.event().data(responseLine));
                }
                emitter.complete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            emitter.completeWithError(e);
        }
    }
    /**
     * 插入Q&A到Qanything知识库
     *
     * @param question 用户输入的问题内容
     * @param answer 用户输入的答案内容
     * @return 包含检索文档的JSONObject，如果请求失败，则返回null
     */

    public boolean insertQA(String question, String answer) {
        String url = "http://" + serverIp + ":" + serverPort + "/api/local_doc_qa/upload_faqs";
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", "zzp");
        requestBody.put("kb_id", "KB71908604ea5546778627de248098dd5e_240625_FAQ");
        requestBody.put("chunk_size", 800);
        List<Map<String, Object>> faqs = new ArrayList<>();
        Map<String, Object> faq = new HashMap<>();
        faq.put("question", question);
        faq.put("answer", answer);
        faqs.add(faq);
        requestBody.put("faqs", faqs);
        String outputStr = JSON.toJSONString(requestBody);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = outputStr.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
