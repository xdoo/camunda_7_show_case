package de.xapio.demo.services.basedata;

import com.google.common.collect.Lists;
import de.xapio.demo.processes.GenericBillingVars;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.SpinList;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class SaveBillService extends AbstractBasedataService {

    @Value("${directus.url.bill}")
    private String billUrl;

    @Value("${directus.url.type}")
    private String typeUrl;

    private final Map<String,String> types = new HashMap<>();

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        JacksonJsonNode bill = (JacksonJsonNode) delegate.getVariable(GenericBillingVars.BILL);
        String contractId = delegate.getVariable(GenericBillingVars.CONTRACT_ID).toString();

        String rechnungsDatum = bill.prop("rechnungs_datum").stringValue();
        String nettoBetrag = bill.prop("netto_betrag").stringValue();
        String bruttoBetrag = bill.prop("brutto_betrag").stringValue();
        String steuer = bill.prop("steuer").stringValue();
        String rechnungsNummer = bill.prop("rechnungs_nr").stringValue();
        String raw = bill.toString();
        ArrayList<SpinJsonNode> billItems = this.createBillItems(bill);
        String items = this.stringifyBillItems(billItems);

        String payload = String.format(" {\n" +
                "    \"date\": \"%s\",\n" +
                "    \"net_amount\": %s,\n" +
                "    \"gross_amount\": %s,\n" +
                "    \"tax\": %s,\n" +
                "    \"number\": \"%s\",\n" +
                "    \"contract\": \"%s\",\n" +
                "    \"raw\": %s,\n" +
                "    \"bill_items\": [\n" +
                "        %s" +
                "    ],\n" +
                "    \"status\": \"published\"\n" +
                "}", rechnungsDatum, nettoBetrag, bruttoBetrag, steuer, rechnungsNummer, contractId, raw, items);
        log.info(payload);

        String response = this.restTemplate.postForObject(this.billUrl, payload, String.class);
        log.info(response);

        // set bill id as process var
        String billId = JSON(response).prop("data").prop("id").stringValue();
        delegate.setVariable(GenericBillingVars.BILL_ID, billId);

        // set bill items as process var
        List<String> billItemVars = billItems.stream().map(b -> b.toString()).collect(Collectors.toList());
        delegate.setVariable(GenericBillingVars.BILL_ITEMS, billItemVars);

        // set bill id as bussiness key - so the correlation can be unique
        delegate.setProcessBusinessKey(billId);

        // set some other vars
        delegate.setVariable(GenericBillingVars.PRICE_GROSS, bruttoBetrag);
        delegate.setVariable(GenericBillingVars.PRICE_NET, nettoBetrag);
        delegate.setVariable(GenericBillingVars.PRICE_TAX, steuer);
    }

    public ArrayList<SpinJsonNode> createBillItems(JacksonJsonNode bill) {
        List<String> props = bill.fieldNames();
        Map<String, SpinJsonNode> items = new HashMap<>();

        props.stream().forEach(p -> {
            this.types.keySet().forEach(k -> {
                if(p.startsWith(k)) {
                    String[] s = p.split("_");
                    String value = bill.prop(p).stringValue();
                    this.buildBillItem(items, k, s[1], value);
                }
            });
        });

        return Lists.newArrayList(items.values());
    }

    public String stringifyBillItems(ArrayList<SpinJsonNode> itemObjects) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i < itemObjects.size();i++){
            if(i>0) {
                sb.append(",");
            }
            sb.append(itemObjects.get(i));
        }

        return sb.toString();
    }

    public void buildBillItem(Map<String, SpinJsonNode> items, String typePrefix, String prop, String value) {
        // is there an entry?
        if(!items.containsKey(typePrefix)) {
            SpinJsonNode json = JSON("{\n" +
                    "            \"status\": \"published\",\n" +
                    "            \"type\": \"\",\n" +
                    "            \"amount\": 1,\n" +
                    "            \"price\": 0\n" +
                    "        }");
            // set type id
            json.prop("type", this.types.get(typePrefix));
            items.put(typePrefix, json);
        }
        SpinJsonNode item = items.get(typePrefix);

        // preis?
        if(prop.toLowerCase().equals("preis")) {
            item.prop("price", value);
        }
        // anzahl?
        if(prop.toLowerCase().equals("verbrauch")) {
            item.prop("amount", value);
        }
    }

    @PostConstruct
    public void loadTypes() {
        // load types in map
        String response = this.restTemplate.getForObject(typeUrl + "?fields=id,name,prefix", String.class);
        SpinJsonNode typeJson = JSON(response);
        if(typeJson.prop("data").isArray()){
            SpinList<SpinJsonNode> t = typeJson.prop("data").elements();
            t.forEach(type -> {
                this.types.put(type.prop("prefix").stringValue(), type.prop("id").stringValue());
            });
        }
    }
}
