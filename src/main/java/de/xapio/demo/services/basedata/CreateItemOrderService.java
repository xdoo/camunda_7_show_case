package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service @Slf4j
public class CreateItemOrderService extends AbstractBasedataService {

    @Value("${directus.url.item_order}")
    private String url;

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        String itemId = delegate.getVariable("item").toString();
        String orderId = delegate.getBusinessKey();
        String orgId = delegate.getVariable("organisation").toString();
        String userId = delegate.getVariable("user_created").toString();
        String date = LocalDateTime.now().toString();

        // create payload
        String payload = String.format("{\n" +
                "    \"date\": \"%s\",\n" +
                "    \"order\": \"%s\",\n" +
                "    \"item\": \"%s\",\n" +
                "    \"organisation\": \"%s\",\n" +
                "    \"status\": \"published\",\n" +
                "    \"user_created\": \"%s\"\n" +
                "}", date, orderId, itemId, orgId, userId);

        // request
        String response = this.restTemplate.postForObject(this.url, payload, String.class);
        log.info("response -> " + response);

        // give vars back to process


    }
}
