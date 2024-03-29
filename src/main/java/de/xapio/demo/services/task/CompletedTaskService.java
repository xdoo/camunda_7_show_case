package de.xapio.demo.services.task;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class CompletedTaskService extends AbstractTaskService {

    private final RuntimeService runtimeService;

    @Value("${directus.url.task}")
    private String url;

    public CompletedTaskService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

    }

    public void completeTask(String taskid) {
        String task = this.restTemplate.getForObject(url + taskid, String.class);
        log.info(task);

        // correlate
        SpinJsonNode jsonTask = JSON(task).prop("data");
        String payload = jsonTask.prop("payload").toString();

        if(jsonTask.hasProp("correlation_key")) {
            String correlationKey = jsonTask.prop("correlation_key").stringValue();
            String[] split = this.splitcorrelationKey(correlationKey);
            JsonValue jsonPayload = SpinValues.jsonValue(payload).create();
            this.runtimeService.createMessageCorrelation(split[1])
                    .processInstanceBusinessKey(split[0])
                    .setVariable("payload", jsonPayload)
                    .correlate();
        } else {
            log.warn("no correlation key found!");
        }

    }
}
