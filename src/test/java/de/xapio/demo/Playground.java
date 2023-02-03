package de.xapio.demo;

import lombok.extern.slf4j.Slf4j;
import org.camunda.spin.json.SpinJsonNode;
import org.junit.jupiter.api.Test;

import static org.camunda.spin.Spin.JSON;

@Slf4j
public class Playground {

    @Test
    public void testSplit() {
        String s = "foo&&bar";
        String[] split = s.split("&&");
        log.info(split[0]);
        log.info(split[1]);
    }

    @Test
    public void jsonSpin() {
        String json = "{\n" +
            "            \"status\": \"published\",\n" +
            "            \"type\": \"ee4c6f2f-621b-4498-ad22-d7b4e0e52624\",\n" +
            "            \"amount\": 12,\n" +
            "            \"price\": 225\n" +
            "        }";

        SpinJsonNode jsonNode = JSON(json);
        String jsonString = jsonNode.toString();
        log.info(jsonString);
    }

}
