package de.xapio.demo.services.documentation;

import com.google.common.collect.Lists;
import de.xapio.demo.model.DocAtts;
import de.xapio.demo.services.basedata.LoadItemService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.SpinList;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class CreateProcessService implements JavaDelegate {

    private RestTemplate restTemplate;

    @Value("${elastic.url}")
    private String url;

    // HACK 4 DEMO! no error handling, no beans,...
    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        SpinJsonNode doc = JSON("{\"events\": []}");
        // add some metadata
        doc.prop("order_id", delegate.getProcessBusinessKey());
        doc.prop(DocAtts.ITEM_NAME, delegate.getVariable(LoadItemService.ITEM_NAME).toString());
        doc.prop(DocAtts.ITEM_ID, delegate.getVariable(LoadItemService.ITEM_ID).toString());
        doc.prop(DocAtts.ITEM_PRICE, delegate.getVariable(LoadItemService.ITEM_PRICE).toString());
        doc.prop(DocAtts.LOT, delegate.getVariable(LoadItemService.ITEM_LOT).toString());
        doc.prop(DocAtts.PERIOD, delegate.getVariable(LoadItemService.ITEM_PERIOD).toString());
        doc.prop(DocAtts.STATE, "eingegangen");

        // create event
        SpinJsonNode event = JSON("{\"event_type\": \"opened-order\"}");
        event.prop("published_at", LocalDateTime.now().toString());
        event.prop("published_by", "Hans Tester");

        // add event to list
        SpinJsonNode eventsJson = doc.prop("events");
        SpinList<SpinJsonNode> events = eventsJson.elements();
        events.add(event);
        doc.prop("events", Lists.newArrayList(events));

        // make http request
        String uri = this.url + "baykom/_doc";
        HttpEntity<String> request = new HttpEntity<>(doc.toString());
        String result = this.restTemplate.postForObject(uri, request, String.class);

        log.info(result);
    }

    @PostConstruct
    private void init() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.restTemplate = restTemplate;
    }
}
