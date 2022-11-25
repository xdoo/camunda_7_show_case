package de.xapio.demo.services;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service @Slf4j
public class GenericProcessStarter implements JavaDelegate {
    public final static String PAYLOAD = "payload";
    public final static String CALLED_BY = "called_by";

    private final RuntimeService runtimeService;

    public GenericProcessStarter(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // den 'process key' des aufzurufenden Prozesses holen
        String processKey = (String) delegate.getVariable("call_process");

        // die payload und business key holen
        Object payload = delegate.getVariable(PAYLOAD);
        final String calledBy = delegate.getProcessDefinitionId();

        log.info("Prozess " + processKey + " wird aufgerufen");
        ProcessInstance instance = this.runtimeService.createProcessInstanceByKey(processKey)
                .setVariable(PAYLOAD, payload)
                .setVariable(CALLED_BY, calledBy)
                .execute();
        log.info("Definition ID -> " + instance.getProcessDefinitionId());
    }
}
