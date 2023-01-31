package de.xapio.demo.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class BillImporter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:inbox_bills?move=.done")
                .to("file:outbox?charset=utf-8");
//                .to("log:import.bill?showAll=true");
    }
}
