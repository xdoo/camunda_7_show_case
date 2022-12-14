package de.xapio.demo.services.documentation;

import com.google.common.collect.Lists;
import de.xapio.demo.model.DocAtts;
import de.xapio.demo.services.basedata.LoadItemService;
import de.xapio.demo.services.basedata.LoadOrganisationService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.SpinList;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class CreateProcessService extends AbstractDocumentationService {

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
        
        // add some geodata
        doc.prop(DocAtts.ORG_TYPE, delegate.getVariable(LoadOrganisationService.ORG_TYPE).toString());
        doc.prop(DocAtts.ORG_NAME, delegate.getVariable(LoadOrganisationService.ORG_NAME).toString());
        SpinJsonNode address = JSON(delegate.getVariable(LoadOrganisationService.ORG_ADDRESS).toString());
        doc.prop(DocAtts.ORG_LOCATION, address.prop("location"));
        doc.prop(DocAtts.ORG_ZIP_CODE, address.prop("zip_code"));
        doc.prop(DocAtts.ORG_STREET, address.prop("street"));
        doc.prop(DocAtts.ORG_STREET_NR, address.prop("street_number"));
        doc.prop(DocAtts.ORG_DISTRICT, address.prop("district"));
        doc.prop(DocAtts.ORG_STATE, address.prop("state"));
        doc.prop(DocAtts.ORG_COUNTRY, address.prop("country"));
        doc.prop(DocAtts.ORG_COUNTRY_CODE, address.prop("country_code"));
        doc.prop(DocAtts.ORG_GEOHASH, address.prop("geohash"));
        doc.prop(DocAtts.ORG_LAT_LON, Lists.newArrayList(address.prop("lat"), address.prop("lon")));

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
}
