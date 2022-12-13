package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        String itemId = delegate.getVariable(ITEM_ID).toString();

        String uri = this.url + itemId + "?populated=%2A";

        String result = this.restTemplate.getForObject(uri, String.class);
        log.info(result);
    }
}
