package de.xapio.demo.services.basedata;

import de.xapio.demo.processes.GenericBillingVars;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service @Slf4j
public class BillEventService extends AbstractBasedataService {
    @Value("${directus.url.bill_event}")
    private String billEventUrl;

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        String date = LocalDateTime.now().toString();
        String billId = delegate.getVariable(GenericBillingVars.BILL_ID).toString();
        String name = delegate.getVariable("name").toString();
        String type = delegate.getVariable("type").toString();
        String message = delegate.getVariable("message").toString();

        String payload = String.format("{\n" +
                "    \"name\": \"%s\",\n" +
                "    \"type\": \"%s\",\n" +
                "    \"date\":\"%s\",\n" +
                "    \"message\": \"%s\",\n" +
                "    \"bill\": \"%s\",\n" +
                "    \"status\": \"published\"\n" +
                "}", name, type, date, message, billId);

        this.restTemplate.postForObject(this.billEventUrl, payload, String.class);
    }
}
