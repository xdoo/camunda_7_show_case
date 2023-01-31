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

        StringBuilder sb = new StringBuilder("{");
        String businessKey = "";

        ArrayList<String> cols = Lists.newArrayList(header.split(","));
        for(int i=0;i<cols.size();i++) {
            // add a ',' for all cols except the first
            if(i>0) {
                sb.append(",");
            }
            sb.append("\"").append(cols.get(i)).append("\": \"").append(payload.get(i)).append("\"\n");

            // business key
            if(cols.get(i).equals(businessKeyName)) {
                businessKey = payload.get(i);
            }
        }
        sb.append("}");

        String result = String.format("{\n" +
                "   \"businessKey\":\"%s\",\n" +
                "   \"withVariablesInReturn\":false,\n" +
                "   \"process_id\":\"%s\",\n" +
                "   \"variables\":{\n" +
                "      \"bill\":{\n" +
                "         \"value\":%s,\n" +
                "         \"type\":\"object\"\n" +
                "      }\n" +
                "   }\n" +
                "}", businessKey, processId, sb.toString());


        return result;
    }

}
