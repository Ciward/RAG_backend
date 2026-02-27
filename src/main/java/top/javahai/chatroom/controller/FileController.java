package top.javahai.chatroom.controller;

import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.javahai.chatroom.utils.FastDFSUtil;
import top.javahai.chatroom.config.RAGConfig;
import top.javahai.chatroom.utils.AliyunOssUtil;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Map;

/**
 */
@RestController
public class FileController {

  @Value("${fastdfs.nginx.host}")
  String nginxHost;

  @Autowired
  AliyunOssUtil aliyunOssUtil;

  @Autowired
  RAGConfig RAGConfig;

  @PostMapping("/file")
  public String uploadFlie(MultipartFile file) throws IOException, MyException {
    String fileId= FastDFSUtil.upload(file);
    return nginxHost+fileId;
  }

  @PostMapping("/ossFileUpload")
  public String ossFileUpload(@RequestParam("file")MultipartFile file,@RequestParam("module") String module) throws IOException, MyException {
    return aliyunOssUtil.upload(file.getInputStream(),module,file.getOriginalFilename());
  }

  @PostMapping("/local_doc_qa/get_file_base64")
  public ResponseEntity<String> getFileBase64(@RequestBody Map<String, String> requestParams) {
    String fileId = requestParams.get("file_id");
    String base64 = RAGConfig.getFileBase64(fileId);
    return ResponseEntity.ok(base64);
  }

}
