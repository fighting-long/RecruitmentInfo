package my.self.lxh.controller;

import my.self.lxh.task.SYDWJob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static my.self.lxh.constant.Config.JXGW;

/**
 * @author lxh
 * @date 2021-07-02 16:50
 */
@RestController
public class TestController {

    @Autowired
    private SYDWJob sydwJob;

    /**
     * 其实不应该一直向上抛异常的，自己try catch 比较好.
     *
     * @throws IOException
     */
    @GetMapping("/sendMail")
    public Object testSendMail(@RequestParam("test") Boolean send, HttpSession session) throws IOException {
        final ServletContext servletContext = session.getServletContext();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", "success");
        map.put("currentDate", format.format(new Date()));
        if (Objects.nonNull(send) && send) {
            return map;
        }
        URL url = new URL(JXGW);
        Document parse = Jsoup.parse(url, 3000);
        Element list = parse.getElementsByClass("newslist").first();
        Node node = list.childNodes().get(1);
        List<Element> information = node.toString().lines()
                .filter(str -> str.startsWith("<li>"))
                .map(str -> {
                    Element body = Jsoup.parse(str.split("]]></record>")[ 0 ]).body();
                    return Jsoup.parse(
                            body.getElementsByTag("a").first() +
                                    String.format("<span>%s</span>", body.getElementsByClass("date").first().text())
                    ).body();
                }).collect(Collectors.toList());
        sydwJob.sendEmail("江西省人力资源和社会保障厅官网", information);
        return map;
    }


}
