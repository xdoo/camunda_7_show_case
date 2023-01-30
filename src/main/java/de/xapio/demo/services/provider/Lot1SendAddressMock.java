package de.xapio.demo.services.provider;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class Lot1SendAddressMock extends AbstractProviderMockService {

    private final RuntimeService runtimeService;

    public Lot1SendAddressMock(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

    }
}
