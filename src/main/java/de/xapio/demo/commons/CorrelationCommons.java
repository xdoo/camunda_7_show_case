package de.xapio.demo.commons;

public class CorrelationCommons {

    public static String createCorrelationKey(String businessKey, String eventId) {
        return businessKey + "&&" + eventId;
    }

    public static String[] splitcorrelationKey(String correlationKey) {
        return correlationKey.split("&&");
    }
}
