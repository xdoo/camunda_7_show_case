package de.xapio.demo.services.provider;

import de.xapio.demo.services.basedata.LoadItemService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

@Service @Slf4j
public class Lot1ContractServiceMock extends AbstractProviderMockService {

    @Value("${directus.url.contract}")
    private String url;

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get variables
        String date = LocalDateTime.now().toString();
        String orgId = delegate.getVariable("organisation").toString();
        String itemOrder = delegate.getProcessBusinessKey();
        // get address
        // TODO the variable name should be flexible - payload is everything
        String address = delegate.getVariable("payload").toString().replaceAll("\"", "\\\"");
        log.info("Adresse: \n" + address);

        // random value for the distance to the PoP
        Random random = new Random();
        int distance = random.nextInt(25 - 4) + 4;

        // price params
        ArrayList<String> priceParamIds = (ArrayList<String>) delegate.getVariable(LoadItemService.PRICE_PARAM_IDS);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<priceParamIds.size();i++) {
            if(i>0) {
                sb.append(",");
            }
            sb.append("{ \"contract_id\": \"+\", \"price_param_id\": { \"id\": \"").append(priceParamIds.get(i)).append("\" }}");
        }

        // prepare payload
        String payload = String.format("{\n" +
                "   \"contract_date\": \"%s\",\n" +
                "   \"organisation\": \"%s\",\n" +
                "   \"item_order\": \"%s\",\n" +
                "   \"address\": %s,\n" +
                "   \"price_params\": {\n" +
                "       \"create\": [\n" +
                "%s" +
                "        ],\n" +
                "        \"update\": [],\n" +
                "        \"delete\": []\n" +
                "   },\n" +
                "   \"quantity_params\": [\n" +
                "       {\n" +
                "           \"type\": \"ee4c6f2f-621b-4498-ad22-d7b4e0e52624\",\n" +
                "            \"quantity\": %s,\n" +
                "            \"unit\": \"e8b9dce6-99a0-4ce4-9271-1a447ebf78a3\",\n" +
                "            \"status\": \"published\"\n" +
                "       }\n" +
                "   ],\n" +
                "   \"status\": \"published\" \n" +
                "}", date, orgId, itemOrder,address, sb.toString(), distance);

        // set vars for process
        delegate.setVariable("contract", payload);
    }
}
