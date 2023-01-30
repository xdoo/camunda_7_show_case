package de.xapio.demo.services.basedata;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class SaveContractService extends AbstractBasedataService {

    @Value("${directus.url.contract}")
    private String url;
    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        String contract = delegate.getVariable("contract").toString();

        String result = this.restTemplate.postForObject(url, contract, String.class);
        log.info(result);
    }
}
