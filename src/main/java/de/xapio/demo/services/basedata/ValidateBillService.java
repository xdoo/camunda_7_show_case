package de.xapio.demo.services.basedata;

import de.xapio.demo.processes.GenericBillingVars;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class ValidateBillService extends AbstractBasedataService {
    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        JacksonJsonNode bill = (JacksonJsonNode) delegate.getVariable(GenericBillingVars.BILL);

        log.info(bill.toString());


    }
}
