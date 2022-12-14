package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class LoadItemService extends AbstractBasedataService {

    public final static String ITEM_ID = "item_id";
    public final static String ITEM_NAME = "item_name";
    public final static String ITEM_PRICE = "item_price";
    public final static String ITEM_LOT = "item_lot";
    public final static String ITEM_PERIOD = "item_period";

    @Value("${strapi.url.items}")
    private String url;

    // HACK 4 DEMO! no error handling, no beans,...
    @Override
    public void execute(DelegateExecution delegate) throws Exception {
        Object iid = delegate.getVariable(ITEM_ID);
        SpinJsonNode resultJson = this.requestById(this.url, iid);

        // set vars from response
        delegate.setVariable(ITEM_ID, iid);
        delegate.setVariable(ITEM_NAME, resultJson.jsonPath("$.data.attributes.name").stringValue());
        delegate.setVariable(ITEM_PRICE, resultJson.jsonPath("$.data.attributes.price").numberValue());
        delegate.setVariable(ITEM_LOT, resultJson.jsonPath("$.data.attributes.lot.data.attributes.name").stringValue());
        delegate.setVariable(ITEM_PERIOD, resultJson.jsonPath("$.data.attributes.period.data.attributes.name").stringValue());

        log.info(resultJson.toString());
    }
}
