package de.xapio.demo.services;

import com.google.common.primitives.Ints;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class ItemLookupService implements JavaDelegate {

    public final static String NAME = "item_name";
    public final static String PRICE = "item_price";
    public final static String LOS = "item_los";
    public final static String ERR_MSG = "err_msg";
    public final static String ERR_CODE = "err_code";

    @Override
    public void execute(DelegateExecution delegate) throws Exception {
        Object itemId = delegate.getVariable("item_id");
        log.info("item id -> " + itemId.toString());

        Integer item = Ints.tryParse(itemId.toString());

        // Das wäre normalerweise ein DB Lookup oder Service Aufruf.
        switch (item) {
            case 1:
                delegate.setVariable(LOS, "Los 3");
                delegate.setVariable(NAME, "Leistung A");
                delegate.setVariable(PRICE, 500.0);
                break;
            case 2:
                delegate.setVariable(LOS, "Los 3");
                delegate.setVariable(NAME, "Leistung B");
                delegate.setVariable(PRICE, 3500.0);
                break;
            case 3:
                delegate.setVariable(LOS, "Los 2");
                delegate.setVariable(NAME, "Leistung C");
                delegate.setVariable(PRICE, 1760.0);
                break;
            case 4:
                delegate.setVariable(LOS, "Los 1");
                delegate.setVariable(NAME, "Leistung D");
                delegate.setVariable(PRICE, 20000.0);
                break;
            default:
                delegate.setVariable(ERR_MSG, String.format("Es konnte keine Leistung mit der ID '%s' gefunden werden.", item));
                delegate.setVariable(ERR_CODE, 100);
                break;
        }
    }
}
