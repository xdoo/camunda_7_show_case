package de.xapio.demo.services.basedata;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service @Slf4j
public class LoadOrganisationService extends AbstractBasedataService {

    public final static String ORG_ADDRESS = "org_address";
    public final static String ORG_TYPE = "org_type";
    public final static String ORG_NAME = "org_name";

    @Value("${strapi.url.organisations}")
    private String url;

    @Override
    public void execute(DelegateExecution delegate) {

        Object orgId = delegate.getVariable("organisation");
        SpinJsonNode result = this.requestById(this.url, orgId, Lists.newArrayList("main_address", "organisation_type"));

        // publish some data to the process
        SpinJsonNode addressJson = result.jsonPath("$.data.attributes.main_address").element();
        delegate.setVariable(ORG_ADDRESS, addressJson.toString());
        delegate.setVariable(ORG_TYPE, result.jsonPath("$.data.attributes.organisation_type.data.attributes.name").stringValue());
        delegate.setVariable(ORG_NAME, result.jsonPath("$.data.attributes.name").stringValue());

        log.info(result.toString());
    }
}
