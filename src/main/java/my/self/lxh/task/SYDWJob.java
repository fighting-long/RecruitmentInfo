package my.self.lxh.task;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static my.self.lxh.constant.Config.*;

/**
 * 事业单位任务
 *
 * @author lxh
 * @date 2021-06-30 19:25
 */
@Component
public class SYDWJob {

    @Autowired
    private JavaMailSender mailSender;

    private Document getDocument(String resourceUrl) {
        boolean failure = true;
        Document parse = null;
        int count = 0;
        while (failure) {
            try {
                count++;
                parse = Jsoup.parse(new URL(resourceUrl), 5000);
                failure = false;
            } catch (IOException e) {
                if (count > 8) {
                    SimpleMailMessage simpleMessage = new SimpleMailMessage();
                    simpleMessage.setSubject("事业单位爬虫异常");
                    simpleMessage.setText(e.getMessage());
                    simpleMessage.setFrom("343932572@qq.com");
                    simpleMessage.setTo("343932572@qq.com");
                    mailSender.send(simpleMessage);
                }
                count = 0;
            }
        }
        return parse;
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void jxgwJob() {
        Document document = getDocument(JXGW);
        Element list = document.getElementsByClass("newslist").first();
        Node node = list.childNodes().get(1);
        List<Element> information = node.toString().lines()
                .filter(str -> str.startsWith("<li>"))
                .map(str -> {
                    Element body = Jsoup.parse(str.split("]]></record>")[ 0 ]).body();
                    return Jsoup.parse(
                            body.getElementsByTag("a").first() +
                                    String.format("<span>%s</span>", body.getElementsByClass("date").first().text())
                    ).body();
                })
                .filter(element ->{
                    boolean flag = false;
                    String title = element.getElementsByTag("a").first().text();
                    for (String word : KEYWORD){
                        flag = flag || title.contains(word);
                    }
                    return flag &&
                            LocalDate.parse(element.getElementsByTag("span").first().text(), DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
                                    .compareTo(LocalDate.now()) == 0;
                })
                .collect(Collectors.toList());
        // 发送邮件
        sendEmail("江西省人力资源和社会保障厅官网", generateInfo(false,information));
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void srgwJob() {
        Document document = getDocument(SRGW);
        List<Element> information = document.getElementById("doclist").getElementsByTag("li").stream()
                .map(element ->
                        Jsoup.parse(
                    element.getElementsByAttributeValue("target","_blank").first()+
                            String.format("<span>%s</span>", element.getElementsByClass("span_date").first().text())
                        ).body())
                .filter(element -> {
                    boolean flag = false;
                    String title = element.getElementsByTag("a").first().text();
                    for (String word : KEYWORD){
                        flag = flag || title.contains(word);
                    }
                    return flag &&
                            LocalDate.parse(element.getElementsByTag("span").first().text(), DateTimeFormatter.ISO_LOCAL_DATE)
                                    .compareTo(LocalDate.now()) == 0;
                })
                .collect(Collectors.toList());
        // 发送邮件
        sendEmail("上饶市人力资源和社会保障厅官网", generateInfo(true,information));
    }

    public String generateInfo(boolean append,List<Element> information){
        if(information.isEmpty()){
            return null;
        }
        StringBuilder mail = new StringBuilder();
        information.forEach(info ->
                mail.append(String.format("招聘标题：%s | 文章发布时间：%s | 网站网址：%s\n\n",
                        info.getElementsByTag("a").first().text(),
                        info.getElementsByTag("span").first().text(),
                        info.getElementsByTag("a").first().text(
                                info.getElementsByTag("a").first().attr(
                                        "href",append?"http://hrss.zgsr.gov.cn"+info.getElementsByTag("a").first().attr("href"):info.getElementsByTag("a").first().attr("href")
                                ).attr("href")
                        ))
                )
        );
        return mail.toString();
    }

    public void sendEmail(String resource, String mail) {
        if(StringUtils.isBlank(mail)){
            return;
        }
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setSubject("事业单位招聘提醒！！！消息来源：" + resource);
        simpleMessage.setText(mail);
        simpleMessage.setFrom("343932572@qq.com");
        Arrays.stream(TARGETMAIL).forEach(target -> {
            simpleMessage.setTo(target);
            mailSender.send(simpleMessage);
        });
    }



}
