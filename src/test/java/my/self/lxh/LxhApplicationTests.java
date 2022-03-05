package my.self.lxh;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.jupiter.api.Test;
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

    String keyWord = "";
    boolean filter = false;

    @Test
    void srgw() throws IOException {
        URL url = new URL(SRGW);
        Document parse = Jsoup.parse(url, 10000);

        parse.getElementById("doclist").getElementsByTag("li").stream()
                .map(element -> Jsoup.parse(String.valueOf(element.childNode(1)) + element.childNode(2)).body())
                .filter(element ->
                    element.getElementsByTag("a").get(1).text().contains(keyWord) &&
                            LocalDate.parse(element.getElementsByClass("span_date").get(0).text(), DateTimeFormatter.ISO_LOCAL_DATE)
                                .compareTo(LocalDate.now()) == 0)
                .forEach(System.out::println);
    }

    @Test
    void jxgw() throws IOException {
        URL url = new URL(JXGW);
        Document parse = Jsoup.parse(url, 3000);
        Element list = parse.getElementsByClass("newslist").first();
        Node node = list.childNodes().get(1);
        System.out.println(list.getElementsByTag("li"));
        List<Element> collect = node.toString().lines().filter(str -> str.startsWith("<li>"))
                .map((str1) -> {
                    Element body = Jsoup.parse(str1.split("]]></record>")[ 0 ]).body();
                    return Jsoup.parse(
                            body.getElementsByTag("a").first() +
                                    String.format("<span>%s</span>", body.getElementsByClass("date").first().text())
                    ).body();
                })
                .filter(element ->
                        !filter || element.getElementsByTag("a").first().attr("title").contains(keyWord) &&
                                LocalDate.parse(element.getElementsByTag("span").first().text(), DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
                                        .compareTo(LocalDate.now()) == 0).collect(Collectors.toList());

//         发送邮件
//        封装发送邮件方法，将江西官网信息和上饶官网信息都发送
        collect.forEach(System.out::println);

    }

    @Test
    public void test(){

        System.out.println(LocalDate.parse("2022年03月04日", DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
                .compareTo(LocalDate.now()));



    }


}
