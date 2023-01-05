package de.xapio.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TestSplit {

    @Test
    public void testSplit() {
        String s = "foo&&bar";
        String[] split = s.split("&&");
        log.info(split[0]);
        log.info(split[1]);
    }

}
