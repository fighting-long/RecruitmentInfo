package my.self.lxh.hander;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

import static my.self.lxh.constant.Config.TARGETMAIL;

/**
 * @author lxh
 * @date 2021-07-02 16:23
 */
@ControllerAdvice
public class MyExceptionHandler {

    @Autowired
    private JavaMailSender mailSender;

    @ExceptionHandler
    public Object handler(Exception e){
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setText(e.getMessage());
        simpleMessage.setFrom("343932572@qq.com");
        Arrays.stream(TARGETMAIL).forEach(target -> {
            simpleMessage.setTo(target);
            mailSender.send(simpleMessage);
        });
        return e.getMessage();
    }

}
