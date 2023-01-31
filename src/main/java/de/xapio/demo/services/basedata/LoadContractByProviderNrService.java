package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.camunda.spin.plugin.variable.SpinValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        String contract = this.restTemplate.getForObject(filterUrl, String.class);
        // add contract to process context
        delegate.setVariable("contract", SpinValues.jsonValue(contract).create());
    }
}
