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

        String formId = this.getVariableStringValue(delegate,"form_id");
        String reference = this.getVariableStringValue(delegate, "reference");
        String title = this.getVariableStringValue(delegate, "title");
        String orgId = this.getVariableStringValue(delegate, "organisation");



        String correlationId = this.createCorrelationKey(delegate.getBusinessKey(), delegate.getVariable("event_id").toString());

        String payload = String.format("{\n" +
                "    \"form_id\": \"%s\",\n" +
                "    \"reference\": \"%s\",\n" +
                "    \"title\": \"%s\",\n" +
                "    \"organisation\": \"%s\",\n" +
                "    \"status\": \"published\",\n" +
                "    \"correlation_key\": \"%s\"\n" +
                "}", formId, reference, title, orgId, correlationId);

        // request
        String response = this.restTemplate.postForObject(this.url, payload, String.class);
        log.info("response -> " + response);
    }
}
