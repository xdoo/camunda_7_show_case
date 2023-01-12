package de.xapio.demo.services.task;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

abstract public class AbstractTaskService implements JavaDelegate {

    protected  RestTemplate restTemplate;

    @Value("${directus.token}")
    private String token;

    protected String createCorrelationKey(String businessKey, String eventId) {
        return businessKey + "&&" + eventId;
    }

    protected String[] splitcorrelationKey(String correlationKey) {
        return correlationKey.split("&&");
    }

    @PostConstruct
    private void init() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        this.restTemplate = restTemplate;
    }
}
