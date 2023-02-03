package de.xapio.demo.services.basedata;

import de.xapio.demo.processes.GenericBillingVars;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.SpinList;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class CountBillErrorsService extends AbstractBasedataService {

    @Value("${directus.url.bill}")
    private String billUrl;
    
    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        String billId = delegate.getVariable(GenericBillingVars.BILL_ID).toString();

        // load bill
        String url = this.billUrl + billId + "?fields=id,bill_event.type";
        String response = this.restTemplate.getForObject(url, String.class);
        SpinList<SpinJsonNode> billEvents = JSON(response).prop("data").prop("bill_event").elements();

        // load events
        long errors = billEvents.stream().filter(e -> e.prop("type").stringValue().equals("Fehler")).count();

        //send error count to process instance
        delegate.setVariable(GenericBillingVars.ERROR_COUNT, errors);

    }
}
