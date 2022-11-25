package de.xapio.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class GenericProcessStarter implements JavaDelegate {

    private final RuntimeService runtimeService;

    public GenericProcessStarter(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void execute(DelegateExecution delegate) throws Exception {
        Object processId = delegate.getVariable("process_id");
        log.info("Prozess " + processId.toString() + " wird aufgerufen");
    }
}
