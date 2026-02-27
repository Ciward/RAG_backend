# RAG_backend（subtlechat-mini）

基于 Spring Boot 2.3 + Spring Security + MyBatis 的后端服务，提供用户认证、用户管理、问答审核、RAG 流式问答、文件上传等能力。

## 1. 技术栈

- Java 8
- Spring Boot 2.3.1.RELEASE
- Spring Security（表单登录 + JWT 校验）
- MyBatis / MyBatis-Plus
- MySQL 8
- Swagger 2（springfox 2.9.2）
- WebSocket（STOMP）
- FastDFS（可选）
- 阿里云 OSS（可选）

## 2. 代码结构

- `src/main/java/top/javahai/chatroom/config`：安全配置、CORS、验证码过滤、RAG/OSS/WebSocket 配置
- `src/main/java/top/javahai/chatroom/controller`：REST API 入口
- `src/main/java/top/javahai/chatroom/security`：登录成功/失败处理器、JWT 认证相关类
- `src/main/java/top/javahai/chatroom/service`：业务服务层
- `src/main/java/top/javahai/chatroom/dao`：MyBatis DAO 接口
- `src/main/resources/mapper`：MyBatis XML SQL
- `src/main/resources/application*.properties`：环境配置

## 3. 核心能力

- 账号体系
- 验证码登录（`/verifyCode` + `/signin`）
- JWT 令牌校验（`Authorization: Bearer <token>` 或 `token` 请求头）
- 用户注册、查询、分页、锁定、删除
- 问题/回答管理与管理员审核
- 审核通过后写入 QAnything FAQ 知识库
- SSE 流式 RAG 问答（`/stream/RAGFileChatStream`）
- 文件上传（FastDFS / OSS）
- 对外代理 `get_file_base64` 接口

## 4. 鉴权与登录流程

1. 先调用 `GET /verifyCode` 获取图片验证码（写入 session: `verify_code`）。
2. 调用 `POST /signin`（`username`、`password`、`code`），成功后返回 `RespBean`，其中包含 `token`。
3. 后续请求携带 token。
4. 调用 `GET /logout` 或 `POST /logout`（Spring Security 默认）执行登出。

说明：
- 登录校验由 `VerificationCodeFilter` 和 `WebSecurityConfig` 控制。
- 大部分接口需登录，放行接口见 `WebSecurityConfig`：
  - `/verifyCode`
  - `/file`
  - `/ossFileUpload`
  - `/user/register`
  - `/user/checkUsername`
  - Swagger 相关路径

## 5. 主要接口（按代码整理）

### 5.1 认证与令牌

- `GET /verifyCode`：获取图形验证码
- `POST /signin`：登录（表单参数）
- `GET /checkToken`：检测 token 是否有效
- `POST /logout`：退出登录

### 5.2 用户

- `POST /user/register`
- `GET /user/getUserInfo`
- `GET /user/checkUsername?username=...`
- `GET /user/selectOne?id=...`
- `GET /user/?page=1&size=10&isLocked=0|1`
- `PUT /user/?id=...&isLocked=true|false`
- `DELETE /user/{id}`
- `DELETE /user/?ids=1&ids=2`

### 5.3 问答（QA）

- `POST /QA/addQuestion`
- `POST /QA/getQuestionsByPage`
- `POST /QA/answerQuestion`
- `POST /QA/getAnswersByQuestionId`
- `POST /QA/checkAnswer`（仅 `role=admin`）
- `POST /QA/setQuestionFinished`

### 5.4 流式 RAG

- `POST /stream/RAGFileChatStream`
  - 返回 `text/event-stream`
  - 由 `RAGConfig#RAGFileChat` 转发 QAnything 流式结果

### 5.5 文件

- `POST /file`：FastDFS 上传
- `POST /ossFileUpload`：OSS 上传（`file` + `module`）
- `POST /local_doc_qa/get_file_base64`：通过后端转发获取文件 base64

## 6. 外部依赖服务

启动前请保证这些依赖可用：

- MySQL：库名 `chatroom`
- QAnything 服务：`http://${app.server.ip}:${app.server.port}`
- LLM API（当前在 `RAGConfig` 里写死）：`http://10.102.33.130:3001/v1`
- SMTP（验证码邮件接口）
- OSS（可选）
- FastDFS（可选）

## 7. 配置说明

当前默认激活 `dev`：

```properties
spring.profiles.active=dev
```

主要配置文件：`src/main/resources/application-dev.properties`

关键项：

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `server.port`（当前是 `8082`）
- `app.server.ip` / `app.server.port`（QAnything 地址）
- `aliyun.oss.*`
- `spring.mail.*`
- `fastdfs.nginx.host`

建议：

- 不要将真实密钥和密码提交到仓库。
- 把敏感配置迁移到环境变量或外部配置中心。

## 8. 本地启动

### 8.1 前置条件

- JDK 8
- Maven 3.6+
- MySQL 8+

### 8.2 启动步骤

```bash
# 1) 安装依赖并编译
mvn clean package -DskipTests

# 2) 运行
mvn spring-boot:run
```

或：

```bash
java -jar target/subtlechat-mini-0.0.1-SNAPSHOT.jar
```

启动后默认地址：

- `http://localhost:8082`

Swagger（若依赖兼容）：

- `http://localhost:8082/swagger-ui.html`

## 9. 返回格式

通用返回对象为 `RespBean`：

```json
{
  "status": 200,
  "msg": "ok",
  "data": {},
  "token": "..."
}
```

分页对象 `RespPageBean` 额外包含：

- `total`
- `data`（列表）

## 10. 典型调用示例

### 10.1 登录（示例）

```bash
curl -X POST 'http://localhost:8082/signin' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'username=demo' \
  --data-urlencode 'password=123456' \
  --data-urlencode 'code=abcd'
```

### 10.2 携带 Token 访问

```bash
curl 'http://localhost:8082/user/getUserInfo' \
  -H 'Authorization: Bearer <your-token>'
```

### 10.3 SSE 流式问答

```bash
curl -N 'http://localhost:8082/stream/RAGFileChatStream' \
  -H 'Authorization: Bearer <your-token>' \
  -H 'Content-Type: application/json' \
  --data '"你的问题内容"'
```

## 11. 已知注意点

- `CorsConfig` 中 `allowedOrigins("*") + allowCredentials(true)` 组合在新版本 Spring 中通常不被允许，若升级需改为白名单域名。
- `RAGConfig` 内存在写死的知识库 ID、模型与 API 地址，建议改为配置化。
- 仓库当前配置文件包含明文敏感信息，建议尽快轮换并清理历史提交。
- `MultiHttpSecurityConfig.java` 当前是历史注释代码，不参与运行。

## 12. 相关文档

- 接口文档：`RAG_API.md`
