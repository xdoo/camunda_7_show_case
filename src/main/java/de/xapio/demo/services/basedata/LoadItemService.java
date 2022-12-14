package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class LoadItemService implements JavaDelegate {

    public final static String ITEM_ID = "item_id";
    public final static String ITEM_NAME = "item_name";
    public final static String ITEM_PRICE = "item_price";
    public final static String ITEM_LOT = "item_lot";
    public final static String ITEM_PERIOD = "item_period";

    private  RestTemplate restTemplate;

    @Value("${strapi.url.items}")
    private String url;
    @Value("${strapi.token.items}")
    private String token;


    // HACK 4 DEMO! no error handling, no beans,...
    @Override
    public void execute(DelegateExecution delegate) throws Exception {
        Object iid = delegate.getVariable(ITEM_ID);
        String uri = this.url + "{id}?populate=*";

        // create request
        String result = this.restTemplate.getForObject(uri, String.class, iid);
        SpinJsonNode resultJson = JSON(result);

        // set vars from response
        delegate.setVariable(ITEM_ID, iid);
        delegate.setVariable(ITEM_NAME, resultJson.jsonPath("$.data.attributes.name").stringValue());
        delegate.setVariable(ITEM_PRICE, resultJson.jsonPath("$.data.attributes.price").numberValue());
        delegate.setVariable(ITEM_LOT, resultJson.jsonPath("$.data.attributes.lot.data.attributes.name").stringValue());
        delegate.setVariable(ITEM_PERIOD, resultJson.jsonPath("$.data.attributes.period.data.attributes.name").stringValue());

        log.info(result);
    }

    @PostConstruct
    private void init() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        this.restTemplate = restTemplate;
    }
}
