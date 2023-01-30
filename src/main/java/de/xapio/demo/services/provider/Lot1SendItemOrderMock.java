package de.xapio.demo.services.provider;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class Lot1SendItemOrderMock extends AbstractProviderMockService {

    private final RuntimeService runtimeService;

    public Lot1SendItemOrderMock(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        log.info("====================================");
        log.info("Leistung wird bei Provider bestellt.");
        log.info("====================================");

        delegate.setVariable("order_accepted", "true");

    }
}
