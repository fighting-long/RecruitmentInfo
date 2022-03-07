package my.self.lxh.controller;

import my.self.lxh.task.SYDWJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author lxh
 * @date 2021-07-02 16:50
 */
@RestController
public class TestController {

    @Autowired
    private SYDWJob sydwJob;

    @GetMapping("/sendMail")
    public Object testSendMail(@RequestParam("test") Boolean send){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", "success");
        map.put("currentDate", format.format(new Date()));
        if (send) {
            return map;
        }
        sydwJob.srgwJob();
        return map;
    }

}
