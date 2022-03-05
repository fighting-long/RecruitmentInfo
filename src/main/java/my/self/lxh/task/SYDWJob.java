package my.self.lxh.task;

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

    @Scheduled(cron = "0 0 23 * * ?")
    public void srgwJob() {
        Document document = getDocument(SRGW);
        List<Element> information = document.getElementById("doclist").getElementsByTag("li").stream()
                .map(element -> Jsoup.parse(String.valueOf(element.childNode(1)) + element.childNode(2)).body())
                .filter(element ->
                        element.getElementsByTag("a").get(1).text().contains(KEYWORD) &&
                                LocalDate.parse(element.getElementsByClass("span_date").get(0).text(), DateTimeFormatter.ISO_LOCAL_DATE)
                                        .compareTo(LocalDate.now()) == 0)
                .collect(Collectors.toList());
        // 发送邮件
        sendEmail("上饶市人力资源和社会保障厅官网", information);
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
                .filter(element ->
                        element.getElementsByTag("a").first().attr("title").contains(KEYWORD) &&
                                LocalDate.parse(element.getElementsByTag("span").first().text(), DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
                                        .compareTo(LocalDate.now()) == 0
                ).collect(Collectors.toList());
        // 发送邮件
        sendEmail("江西省人力资源和社会保障厅官网", information);
    }

    private Document getDocument(String resource) {
        boolean failure = true;
        Document parse = null;
        int count = 0;
        while (failure) {
            try {
                count++;
                parse = Jsoup.parse(new URL(resource), 5000);
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

    public void sendEmail(String resource, List<Element> informations) {
        if(informations.isEmpty()){
            return;
        }
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setSubject("事业单位招聘提醒！！！消息来源：" + resource);
        StringBuilder sb = new StringBuilder();
        informations.forEach(information ->
                sb.append(String.format("招聘标题：%s | 文章发布时间：%s | 网站网址：%s\n\n",
                        information.getElementsByTag("a").first().attr("title"),
                        information.getElementsByTag("span").first().text(),
                        information.getElementsByTag("a").first().attr("href")))
        );
        simpleMessage.setText(sb.toString());
        simpleMessage.setFrom("343932572@qq.com");
        Arrays.stream(TARGETMAIL).forEach(target -> {
            simpleMessage.setTo(target);
            mailSender.send(simpleMessage);
        });
    }



}
