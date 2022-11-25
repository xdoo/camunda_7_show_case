package de.xapio.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class GenericMessageCorrelationService implements JavaDelegate {

    public final static String RESULT = "result";
    public final static String EVENT_NAME = "wait_for_answer";

    private final RuntimeService runtimeService;

    public GenericMessageCorrelationService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        Object result = delegate.getVariable(RESULT);
        log.info("result - " + result.toString());

        this.runtimeService.createMessageCorrelation(EVENT_NAME)
                .processInstanceBusinessKey(delegate.getProcessBusinessKey())
                .setVariable(RESULT, delegate.getVariable(RESULT))
                .correlate();
    }
}
