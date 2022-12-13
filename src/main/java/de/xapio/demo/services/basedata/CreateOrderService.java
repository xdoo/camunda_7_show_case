package de.xapio.demo.services.basedata;

import com.google.common.primitives.Ints;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class CreateOrderService implements JavaDelegate {

    public final static String ITEM_IDS = "item_ids";
    public final static String ORGANISATION = "organisation";

    private final RestTemplate restTemplate;

    @Value("${strapi.url.orders}")
    private String url;

    public CreateOrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        String orderID = RandomStringUtils.randomAlphanumeric(20);
        SpinJsonNode items = JSON(delegate.getVariable(ITEM_IDS));

        // create request object
        SpinJsonNode jsonOrder = JSON("{\"data\": {}}");
        SpinJsonNode jsonData = jsonOrder.prop("data");
        jsonData.prop("order_date", LocalDateTime.now().toString());
        jsonData.prop("order_id", orderID);
        jsonData.prop("items", items);
        jsonData.prop("organisation", (String) delegate.getVariable(ORGANISATION));
        String order = jsonOrder.toString();
        log.info(order);

        // create http request
        HttpEntity<String> request = new HttpEntity<>(order);

        // make http request
        String result = this.restTemplate.postForObject(this.url, request, String.class);
        delegate.setProcessBusinessKey(orderID);

        // cleanse item ids and send to process
        if(items.isArray()) {
            List<Integer> ids = items.elements().stream()
                    .map(n -> n.stringValue())
                    .map(s -> Ints.tryParse(s))
                    .collect(Collectors.toList());
            delegate.setVariable(ITEM_IDS, ids);
        } else {
            log.error("IDs der Leistungen nicht als Array übergeben.");
        }
        log.info(result);
    }
}
