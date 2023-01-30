package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.json.SpinJsonNode;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class CreateItemOrderService extends AbstractBasedataService {

    @Value("${directus.url.item_order}")
    private String url;

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        String itemId = delegate.getVariable("item").toString();
        String orderId = delegate.getBusinessKey();
        String orgId = delegate.getVariable("organisation").toString();
        String userId = delegate.getVariable("user_created").toString();
        String date = LocalDateTime.now().toString();
        // price attributes
        ArrayList<String> ids = (ArrayList<String>) delegate.getVariable(LoadItemService.PRICE_PARAM_IDS);
        StringBuilder sb = new StringBuilder("[ ");
        for(int i=0;i<ids.size();i++) {
            if(i>0) {
                sb.append(",");
            }
            sb.append("{ \"id\": \"").append(ids.get(i)).append("\" }");
        }
        sb.append(" ]");

        // create payload
        String payload = String.format("{\n" +
                "    \"date\": \"%s\",\n" +
                "    \"order\": \"%s\",\n" +
                "    \"item\": \"%s\",\n" +
                "    \"organisation\": \"%s\",\n" +
                "    \"status\": \"published\",\n" +
                "    \"user_created\": \"%s\",\n" +
                "    \"price_param\": %s" +
                "}", date, orderId, itemId, orgId, userId, sb.toString());

        // request
        String response = this.restTemplate.postForObject(this.url, payload, String.class);
        log.info("response -> " + response);

        // give vars back to process
        SpinJsonNode jsonResponse = JSON(response);
        String itemOrderId = jsonResponse.prop("data").prop("id").stringValue();
        delegate.setVariable("item_order_id", itemOrderId);
    }
}
