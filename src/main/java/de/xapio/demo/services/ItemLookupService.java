package de.xapio.demo.services;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service @Slf4j
public class ItemLookupService implements JavaDelegate {

    public final static String ITEM = "item";
    public final static String NAME = "item_name";
    public final static String PRICE = "item_price";
    public final static String LOS = "item_los";
    public final static String ERR_MSG = "err_msg";
    public final static String ERR_CODE = "err_code";

    @Override
    public void execute(DelegateExecution delegate) {
        Object itemId = delegate.getVariable("item_id");
        log.info("item id -> " + itemId.toString());

        Integer item = Ints.tryParse(itemId.toString());

        // Wir geben die Werte als Map zurück, damit sie auch gebündelt an alle Subprozesse weiter gegeben werden können.
        HashMap<String, Object> result = Maps.newHashMap();
        delegate.setVariable(ITEM, result);

        // Das wäre normalerweise eine DB Query oder Service Aufruf.
        switch (item) {
            case 1:
                result.put(LOS, "Los 3");
                result.put(NAME, "Leistung A");
                result.put(PRICE, 500.0);
                break;
            case 2:
                result.put(LOS, "Los 3");
                result.put(NAME, "Leistung B");
                result.put(PRICE, 2500.0);
                break;
            case 3:
                result.put(LOS, "Los 2");
                result.put(NAME, "Leistung C");
                result.put(PRICE, 1760.0);
                break;
            case 4:
                result.put(LOS, "Los 1");
                result.put(NAME, "Leistung D");
                result.put(PRICE, 20000.0);
                break;
            default:
                // TODO - das funktioniert noch nicht richtig
                delegate.setVariable(ERR_MSG, String.format("Es konnte keine Leistung mit der ID '%s' gefunden werden.", item));
                delegate.setVariable(ERR_CODE, 100);
                break;
        }
    }
}
