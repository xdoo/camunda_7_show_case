package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service @Slf4j
public class EventService extends  AbstractBasedataService {

    @Value("${directus.url.order_event}")
    private String url;
    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        String itemOrderId = delegate.getVariable("item_order_id").toString();
        String name = delegate.getVariable("name").toString();
        String type = delegate.getVariable("type").toString();
        String message = delegate.getVariable("message").toString();
        String date = LocalDateTime.now().toString();

        // create payload
        String payload = String.format("{\n" +
                "    \"name\": \"%s\",\n" +
                "    \"type\": \"%s\",\n" +
                "    \"date\": \"%s\",\n" +
                "    \"message\": \"%s\",\n" +
                "    \"item_order\": \"%s\",\n" +
                "    \"status\": \"published\"\n" +
                "}", name, type, date, message, itemOrderId);

        // request
        String response = this.restTemplate.postForObject(this.url, payload, String.class);
        log.info("response (add event) -> " + response);

    }
}
