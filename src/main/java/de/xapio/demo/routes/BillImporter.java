package de.xapio.demo.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.stereotype.Component;

@Component
public class BillImporter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        CsvDataFormat csv = new CsvDataFormat();
        csv.setCaptureHeaderRecord(true);

        from("file:inbox_bills?move=.done")
                .unmarshal(csv)
                .split(body())
                .bean("lineToJsonConverterService", "convert('generic_billing', 'vertrags_nr', ${header[CamelCsvHeaderRecord]}, ${body})")
                .log("${body}")
                .bean("genericProcessStarter", "startProcess(${body})")
        ;
    }
}
