package top.javahai.chatroom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import top.javahai.chatroom.service.NoticeService;
/**
 * 爬虫, 估计用不到了
 */
@Component
@Slf4j
public class CrawlerConfig {

    @Autowired
    private NoticeService noticeService;
    /*
    public static Document newsHome(String url){

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        Document document = null;
        try {

            HttpGet request = new HttpGet(url);
            response = httpClient.execute(request);

            //判断响应状态为200，请求成功，进行处理
            if(response.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = response.getEntity();
                String html = EntityUtils.toString(httpEntity, "utf-8");
                document = Jsoup.parse(html);

            } else {
                System.out.println("返回状态不是200");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return document;
    }

    @PostConstruct
    public void job(){
        
        String url = "https://jsjxsgz.qd.sdu.edu.cn/zytz.htm";
        log.info("爬虫开始");
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("url","http");
        noticeService.remove(queryWrapper);
        List<Notice> notices = new ArrayList<>();
        Document document = newsHome(url);
        Elements elements = document.select("a");
        for (Element element : elements){
            String href = element.attr("href");
            // 筛选新闻地址
            if(href.contains("info")){
                String a = element.text();
                log.info("爬取通知标题：{}",a);
                System.out.println(href);
                Notice notice = new Notice();
                notice.setTitle(a);
                notice.setUrl("https://jsjxsgz.qd.sdu.edu.cn/"+href);
                notice.setDate(new Date());
                notices.add(notice);
            }
        }
        noticeService.saveBatch(notices);
        log.info("爬虫结束");
    }
    
    public static void main(String[] args) {

        String url = "https://jsjxsgz.qd.sdu.edu.cn/zytz.htm";
        log.info("爬虫开始");
        Document document = newsHome(url);
        Elements elements = document.select("a");
        for (Element element : elements){
            String href = element.attr("href");
            // 筛选新闻地址
            if(href.contains("info")){
                String a = element.text();
                log.info("爬取通知标题：{}",a);
                System.out.println(href);
            }
        }
        log.info("爬虫结束");
    }
    */
}
