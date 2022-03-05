package my.self.lxh.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author lxh
 * @date 2021-06-30 19:26
 */
@PropertySource(value = {"classpath:constant.properties"})
@Component
public class Config {
    @Value("${jxgw}")
    public String jxgw;
    @Value("${srgw}")
    public String srgw;
    @Value("${keyword}")
    public String keyword;
    @Value("${targetMail}")
    public String[] targetMail;

    public static String JXGW;
    public static String SRGW;
    public static String KEYWORD;
    public static String[] TARGETMAIL;
    @PostConstruct
    public void init(){
        JXGW = jxgw;
        SRGW = srgw;
        KEYWORD = keyword;
        TARGETMAIL =targetMail;
    }
}
