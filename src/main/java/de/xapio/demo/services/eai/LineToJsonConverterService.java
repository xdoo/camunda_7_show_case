package de.xapio.demo.services.eai;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Hack service for PoC. Think about, if you don't want to have a typed object, here.
 */
@Service @Slf4j
public class LineToJsonConverterService {

    public String convert(String processId, String businessKeyName, String header, List<String> payload) {
        log.info(processId);
        log.info(businessKeyName);
        log.info(header);
        log.info(payload.toString());

        StringBuilder sbJson = new StringBuilder("{");
        String businessKey = "";

        String csv = String.join(",", payload);

        ArrayList<String> cols = Lists.newArrayList(header.split(","));
        for(int i=0;i<cols.size();i++) {
            // add a ',' for all cols except the first
            if(i>0) {
                sbJson.append(",");
            }
            sbJson.append("\"").append(cols.get(i)).append("\": \"").append(payload.get(i).trim()).append("\"\n");

            // business key
            if(cols.get(i).equals(businessKeyName)) {
                businessKey = payload.get(i);
            }
        }
        sbJson.append("}");

        String result = String.format("{\n" +
                "   \"businessKey\":\"%s\",\n" +
                "   \"withVariablesInReturn\":false,\n" +
                "   \"process_id\":\"%s\",\n" +
                "   \"variables\":{\n" +
                "      \"bill\":{\n" +
                "         \"value\":%s,\n" +
                "         \"type\":\"object\"\n" +
                "      },\n" +
                "      \"csv\":{\n" +
                "         \"value\":\"%s\",\n" +
                "         \"type\":\"string\"\n" +
                "      },\n" +
                "      \"bill_provider_number\":{\n" +
                "         \"value\":\"%s\",\n" +
                "         \"type\":\"string\"\n" +
                "      }\n" +
                "   }\n" +
                "}", businessKey, processId, sbJson.toString(), csv, businessKey);

        return result;
    }

}
