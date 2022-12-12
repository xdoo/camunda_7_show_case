package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service @Slf4j
public class OrderService implements JavaDelegate {

    public final static String ITEMS = "items";
    public final static String ORGANISATION = "organisation";

    private final RestTemplate restTemplate;

    @Value("${strapi.url.orders")
    private String url;

    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // create request
        // data
        JSONObject data = new JSONObject();
        data.put("order_date", LocalDateTime.now().toString());
        data.put("order_id", RandomStringUtils.randomAlphanumeric(20));
        // map vars from process
        data.put("items", new JSONArray(delegate.getVariable(ITEMS)));
        data.put("organisation", delegate.getVariable(ORGANISATION));

        JSONObject order = new JSONObject();
        order.put("data", data);

        log.info(order.toString());

        HttpEntity<String> request = new HttpEntity<>(order.toString());

        String result = this.restTemplate.postForObject(this.url, request, String.class);
        log.info(result);
    }
}
