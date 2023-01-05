package de.xapio.demo.services.task;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class CreateTaskService extends AbstractTaskService {

    @Value("${directus.url.task}")
    private String url;

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // prepare payload
        String orgId = delegate.getVariable("organisation").toString();
        String formId = delegate.getVariable("form_id").toString();
        String correlationId = delegate.getBusinessKey() + "&&" + delegate.getVariable("event_id");

        String payload = String.format("{\n" +
                "    \"form_id\": \"%s\",\n" +
                "    \"organisation\": \"%s\",\n" +
                "    \"status\": \"published\",\n" +
                "    \"correlation_key\": \"%s\"\n" +
                "}", formId, orgId, correlationId);

        // request
        String response = this.restTemplate.postForObject(this.url, payload, String.class);
        log.info("response -> " + response);
    }
}
