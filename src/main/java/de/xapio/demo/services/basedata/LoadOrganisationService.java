package de.xapio.demo.services.basedata;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class LoadOrganisationService extends AbstractBasedataService {

    @Value("${strapi.url.organisations}")
    private String url;

    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        Object orgId = delegate.getVariable("organisation");
        SpinJsonNode result = this.requestById(this.url, orgId, Lists.newArrayList("main_address", "organisation_type"));

        log.info(result.toString());
    }
}
