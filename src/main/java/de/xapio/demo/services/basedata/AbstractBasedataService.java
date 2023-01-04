package de.xapio.demo.services.basedata;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import static org.camunda.spin.Spin.JSON;

abstract public class AbstractBasedataService implements JavaDelegate {

    protected  RestTemplate restTemplate;

    @Value("${directus.token}")
    private String token;

    /**
     * find something by id - populate all
     * HACK - id is da database generated key.
     *
     * @param url
     * @param id
     * @return
     */
    protected SpinJsonNode requestById(String url, Object id) {
        ArrayList<String> list = new ArrayList<>();
        return JSON(this.requestById(url, id, list));
    }

    /**
     * find something by id - populate given props
     * HACK - id is da database generated key.
     *
     * @param url
     * @param id
     * @param populatedProps
     * @return
     */
    protected SpinJsonNode requestById(String url, Object id, List<String> populatedProps) {
        // create url params
        StringBuilder sb = new StringBuilder(url).append("{id}").append("?");
        if(populatedProps.size() > 0) {
            for(int i=0;i<populatedProps.size();i++) {
                sb.append((i>0) ? "&" : "").append("populate[").append(populatedProps.get(i)).append("]=*");
            }
        } else {
            sb.append("populate=*");
        }

        // create request
        String uri = sb.toString();
        String result = this.restTemplate.getForObject(uri, String.class, id);
        return JSON(result);
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
