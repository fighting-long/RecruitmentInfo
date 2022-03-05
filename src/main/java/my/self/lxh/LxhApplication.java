package my.self.lxh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LxhApplication {

    public static void main(String[] args) {
        SpringApplication.run(LxhApplication.class, args);
    }

}
