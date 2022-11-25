package de.xapio.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class DocumentationService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegate) throws Exception {
        String businessKey = RandomStringUtils.randomAlphanumeric(20);
        delegate.setProcessBusinessKey(businessKey);
    }
}
