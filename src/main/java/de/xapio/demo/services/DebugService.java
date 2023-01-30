package de.xapio.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class DebugService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegate) throws Exception {
        log.info(delegate.getProcessBusinessKey());
    }
}
