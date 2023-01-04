package de.xapio.demo.api;

import de.xapio.demo.services.GenericProcessStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping(value = "/webhook")
public class WebhookEndpoint {

    private final GenericProcessStarter processStarter;

    public WebhookEndpoint(GenericProcessStarter processStarter) {
        this.processStarter = processStarter;
    }

    @PostMapping("/create")
    public void getWeebhookEventForCreate(@RequestBody String msg) {
        log.info(msg);
        this.processStarter.startProcess(msg);
    }
}
