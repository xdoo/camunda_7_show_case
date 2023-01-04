package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class LoadItemService extends AbstractBasedataService {

    public final static String ITEM_ID = "item_id";
    public final static String ITEM_NAME = "item_name";
    public final static String ITEM_PRICE = "item_price";
    public final static String ITEM_LOT_NAME = "item_lot_name";
    public final static String ITEM_LOT_LABEL = "item_lot_label";
    public final static String ITEM_PERIOD = "item_period";
    public final static String PRICE_PARAM_IDS = "price_param_ids";

    @Value("${directus.url.item}")
    private String url;

    // HACK 4 DEMO! no error handling, no beans,...
    @Override
    public void execute(DelegateExecution delegate) throws Exception {
        Object iid = delegate.getVariable(ITEM_ID);
        log.info("load item details for " + iid);

        String filters = "?fields=name,price,lot.name,lot.label,period.name,price_param.id";
        StringBuilder sb = new StringBuilder(this.url).append(iid.toString()).append(filters);

        String result = this.restTemplate.getForObject(sb.toString(), String.class);
        SpinJsonNode json = JSON(result);
        SpinJsonNode data = json.prop("data");

        // set vars from response
        delegate.setVariable(ITEM_ID, iid);
        delegate.setVariable(ITEM_NAME, data.prop("name").stringValue());
        delegate.setVariable(ITEM_LOT_NAME, data.prop("lot").prop("name").stringValue());
        delegate.setVariable(ITEM_LOT_LABEL, data.prop("lot").prop("label").stringValue());
        delegate.setVariable(ITEM_PERIOD, data.prop("period").prop("name").stringValue());

        // price parameters
        List<String> priceParamIds = new ArrayList<>();
        data.prop("price_param").elements().forEach(id -> {
            priceParamIds.add(id.prop("id").stringValue());
        });
        delegate.setVariable(PRICE_PARAM_IDS, priceParamIds);
    }
}
