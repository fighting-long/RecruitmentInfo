package my.self.lxh;

import my.self.lxh.task.SYDWJob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static my.self.lxh.constant.Config.JXGW;
import static my.self.lxh.constant.Config.SRGW;


@SpringBootTest
class LxhApplicationTests {

    @Autowired
    private SYDWJob sydwJob;

    String keyWord = "";

    boolean filter = false;

    @Test
    void jxgw() throws IOException {
        URL url = new URL(JXGW);
        Document parse = Jsoup.parse(url, 3000);
        Element list = parse.getElementsByClass("newslist").first();
        Node node = list.childNodes().get(1);
        System.out.println(list.getElementsByTag("li"));
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
                        !filter || element.getElementsByTag("a").first().attr("title").contains(keyWord) &&
                                LocalDate.parse(element.getElementsByTag("span").first().text(), DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
                                        .compareTo(LocalDate.now()) == 0)
                .collect(Collectors.toList());
        System.out.println(sydwJob.generateInfo(false,information));
    }

    @Test
    void srgw() throws IOException {
        URL url = new URL(SRGW);
        Document document = Jsoup.parse(url, 10000);
        List<Element> information = document.getElementById("doclist").getElementsByTag("li").stream()
                .map(element ->
                        Jsoup.parse(
                    element.getElementsByAttributeValue("target","_blank").first()+
                            String.format("<span>%s</span>", element.getElementsByClass("span_date").first().text())
                        ).body())
                .filter(element ->
                        !filter || element.getElementsByTag("a").first().text().contains(keyWord) &&
                                LocalDate.parse(element.getElementsByTag("span").first().text(), DateTimeFormatter.ISO_LOCAL_DATE)
                                        .compareTo(LocalDate.now()) == 0)
                .collect(Collectors.toList());
        System.out.println(sydwJob.generateInfo(true,information));
    }


    @Test
    public void test(){

//        System.out.println(LocalDate.parse("2022年03月04日", DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
//                .compareTo(LocalDate.now()));
        sydwJob.srgwJob();


    }


}
