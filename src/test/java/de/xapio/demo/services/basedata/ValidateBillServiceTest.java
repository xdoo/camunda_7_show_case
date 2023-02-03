package de.xapio.demo.services.basedata;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidateBillServiceTest {

    private ValidateBillService service = new ValidateBillService();

    @Test
    void checkNet() {
        List<String> errorStack = new ArrayList<>();
        List<String> billItems = new ArrayList<>();
        billItems.add("{\"status\":\"published\",\"type\":\"ee4c6f2f-621b-4498-ad22-d7b4e0e52624\",\"amount\":\"12.0\",\"price\":\"225.0\"}");
        billItems.add("{\"status\":\"published\",\"type\":\"b9c82c94-19e0-4279-b0aa-1e9fa7614ce8\",\"amount\":\"92.0\",\"price\":\"373.1\"}");
        billItems.add("{\"status\":\"published\",\"type\":\"e9b2e965-84c3-49e1-9283-2c8fa3ec2306\",\"amount\":1,\"price\":\"20.0\"}");
        String priceNet = "618.1";
        this.service.checkNet(errorStack, billItems, priceNet);
        assertEquals(0, errorStack.size());
    }

    @Test
    void checkNetError() {
        List<String> errorStack = new ArrayList<>();
        List<String> billItems = new ArrayList<>();
        billItems.add("{\"status\":\"published\",\"type\":\"ee4c6f2f-621b-4498-ad22-d7b4e0e52624\",\"amount\":\"12.0\",\"price\":\"225.0\"}");
        billItems.add("{\"status\":\"published\",\"type\":\"b9c82c94-19e0-4279-b0aa-1e9fa7614ce8\",\"amount\":\"92.0\",\"price\":\"373.1\"}");
        billItems.add("{\"status\":\"published\",\"type\":\"e9b2e965-84c3-49e1-9283-2c8fa3ec2306\",\"amount\":1,\"price\":\"20.0\"}");
        String priceNet = "620.1";
        this.service.checkNet(errorStack, billItems, priceNet);
        assertEquals(1, errorStack.size());
    }

    @Test
    void checkGross() {
        List<String> errorStack = new ArrayList<>();
        String priceNet = "10.00";
        String priceTax = "2.00";
        String priceGross = "12.00";
        this.service.checkGross(errorStack, priceNet, priceTax, priceGross);
        assertEquals(0, errorStack.size());
    }

    @Test
    void checkGrossError() {
        List<String> errorStack = new ArrayList<>();
        String priceNet = "10.00";
        String priceTax = "2.00";
        String priceGross = "13.00";
        this.service.checkGross(errorStack, priceNet, priceTax, priceGross);
        assertEquals(1, errorStack.size());
    }

    @Test
    void checkTax() {
        List<String> errorStack = new ArrayList<>();
        String priceNet = "100";
        String priceTax = "19";
        this.service.checkTax(errorStack, priceNet, priceTax);
        assertEquals(0, errorStack.size());
    }

    void checkTaxError() {
        List<String> errorStack = new ArrayList<>();
        String priceNet = "100";
        String priceTax = "18.5";
        this.service.checkTax(errorStack, priceNet, priceTax);
        assertEquals(1, errorStack.size());
    }
}