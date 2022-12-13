package de.xapio.demo.services.basedata;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplateHandler;

import java.util.HashMap;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class LoadItemService implements JavaDelegate {

    public final static String ITEM_ID = "item_id";

    private final RestTemplate restTemplate;

    @Value("${strapi.url.items}")
    private String url;

    public LoadItemService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution delegate) throws Exception {
        Object iid = delegate.getVariable(ITEM_ID);
        String uri = this.url + "{id}?populate=*";

        String result = this.restTemplate.getForObject(uri, String.class, iid);
        SpinJsonNode resultJson = JSON(result);

        delegate.setVariable("item_id", iid);
        delegate.setVariable("item_name", resultJson.jsonPath("$.data.attributes.name").stringValue());
        delegate.setVariable("item_price", resultJson.jsonPath("$.data.attributes.price").numberValue());
        delegate.setVariable("item_lot", resultJson.jsonPath("$.data.attributes.lot.data.attributes.name").stringValue());
        delegate.setVariable("item_period", resultJson.jsonPath("$.data.attributes.period.data.attributes.name").stringValue());

        log.info(result);
    }
}
