package de.xapio.demo.services.basedata;

import de.xapio.demo.processes.GenericBillingVars;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.json.SpinJsonPathQuery;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class LoadContractByProviderNrService extends AbstractBasedataService {

    @Value("${directus.url.contract}")
    private String url;
    @Override
    public void execute(DelegateExecution delegate) throws Exception {
        String providerContractNr = delegate.getProcessBusinessKey();
        // create Filter URL
        String filterUrl = String.format("%s?filter[provider_contract_number][_eq]=%s&fields=id,provider_contract_number," +
                "organisation.id,item_order.item.provider_item_number,price_params.price_param_id.price_per_unit," +
                "price_params.price_param_id.free_units,price_params.price_param_id.cost_cap," +
                "price_params.price_param_id.period,price_params.price_param_id.type.name," +
                "price_params.price_param_id.type.prefix,quantity_params.quantity,quantity_params.type.name," +
                "quantity_params.type.prefix",this.url, providerContractNr);
        // load Contract
        String response = this.restTemplate.getForObject(filterUrl, String.class);

        SpinJsonNode jsonResponse = JSON(response);
        if(jsonResponse.hasProp("data")) {

            // add contract to process context
            SpinJsonNode contract = jsonResponse.jsonPath("$.data[0]").element();
            delegate.setVariable(GenericBillingVars.CONTRACT, SpinValues.jsonValue(contract).create());

            // add contract id to process context
            String contractId = jsonResponse.jsonPath("$.data[0].id").stringValue();
            delegate.setVariable(GenericBillingVars.CONTRACT_ID, contractId);

            // add provider item number to process context
            String itemNumber = jsonResponse.jsonPath("$.data[0].item_order.item.provider_item_number").stringValue();
            delegate.setVariable(GenericBillingVars.ITEM_NUMBER, itemNumber);
        } else {
            log.warn("response seems not ok: " + response);
        }


    }
}
