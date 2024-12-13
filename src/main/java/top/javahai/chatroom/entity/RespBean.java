package top.javahai.chatroom.entity;

/**
 */
//JSON返回值的实体类
public class RespBean {
  private Integer status;//状态码
  private String msg;//返回消息
  private Object data;//返回实体
  private String token;//返回Token
  public static RespBean build(){
    return new RespBean();
  }

  public static RespBean ok(String msg){
    return new RespBean(200,msg,null);
  }
  public static RespBean ok(String msg,Object data){
    return new RespBean(200,msg,data);
  }

  public static RespBean error(String msg){
    return new RespBean(500,msg,null);
  }
  public static RespBean error(String msg,Object data){
    return new RespBean(500,msg,data);
  }

  private RespBean(){

  }

  private RespBean(Integer status, String msg, Object data) {
    this.status = status;
    this.msg = msg;
    this.data = data;
  }

  public Integer getStatus() {
    return status;
  }

  public RespBean setStatus(Integer status) {
    this.status = status;
    return this;
  }

  public String getMsg() {
    return msg;
  }

  public RespBean setMsg(String msg) {
    this.msg = msg;
    return this;
  }

  public Object getObj() {
    return data;
  }

  public RespBean setObj(Object data) {
    this.data = data;
    return this;
  }

  public String getToken() {
    return token;
  }

  public RespBean setToken(String token) {
    this.token = token;
    return this;
  }
}
