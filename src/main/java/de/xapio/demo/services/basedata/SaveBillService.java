package de.xapio.demo.services.basedata;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import de.xapio.demo.processes.GenericBillingVars;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class SaveBillService extends AbstractBasedataService {

    @Value("${directus.url.contract}")
    private String contractUrl;

    @Value("${directus.url.type}")
    private String typeUrl;
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
        String raw = "{}";

        String payload = String.format(" {\n" +
                "    \"date\": \"%s\",\n" +
                "    \"net_amount\": %s,\n" +
                "    \"gross_amount\": %s,\n" +
                "    \"tax\": %s,\n" +
                "    \"number\": \"%s\",\n" +
                "    \"contract\": \"%s\",\n" +
                "    \"raw\": %s,\n" +
                "    \"bill_items\": [\n" +
                "        {\n" +
                "            \"status\": \"published\",\n" +
                "            \"type\": \"ee4c6f2f-621b-4498-ad22-d7b4e0e52624\",\n" +
                "            \"amount\": 12,\n" +
                "            \"price\": 225\n" +
                "        },\n" +
                "        {\n" +
                "            \"status\": \"published\",\n" +
                "            \"type\": \"e9b2e965-84c3-49e1-9283-2c8fa3ec2306\",\n" +
                "            \"amount\": 1,\n" +
                "            \"price\": 20\n" +
                "        }\n" +
                "    ],\n" +
                "    \"status\": \"published\"\n" +
                "}", rechnungsDatum, nettoBetrag, bruttoBetrag, steuer, rechnungsNummer, contractId, raw);

        log.info(payload);
    }
}
