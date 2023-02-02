package de.xapio.demo.services.basedata;

import org.camunda.spin.json.SpinJsonNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.camunda.spin.Spin.JSON;
import static org.junit.jupiter.api.Assertions.*;

class ValidatePriceParamServiceTest {

    private ValidatePriceParamService service = new ValidatePriceParamService();

    @Test
    void checkQuantityParamValid() {
        List<String> errorStack = new ArrayList<>();
        SpinJsonNode quantityParam = JSON("{}");
        quantityParam.prop("amount", 10.3);
        SpinJsonNode billItem = JSON("{}");
        billItem.prop("amount", 10.3);
        this.service.checkQuantityParam(errorStack, quantityParam, billItem);
        assertTrue(errorStack.isEmpty());
    }

    @Test
    void checkQuantityParamError() {
        List<String> errorStack = new ArrayList<>();
        SpinJsonNode quantityParam = JSON("{}");
        quantityParam.prop("amount", 10.3);
        SpinJsonNode billItem = JSON("{}");
        billItem.prop("amount", 14);
        this.service.checkQuantityParam(errorStack, quantityParam, billItem);
        assertTrue(errorStack.size() == 1);
    }

    @Test
    void testCheckPrice() {
        // Beispieldaten erstellen
        List<String> errorStack = new ArrayList<String>();
        SpinJsonNode priceParam = JSON("{}");
        priceParam.prop("free_units", 5);
        priceParam.prop("cost_cap", -1);
        priceParam.prop("price_per_unit", 10.00);
        SpinJsonNode billItem = JSON("{}");
        billItem.prop("amount", 10);
        billItem.prop("price", 50.00);

        // Methode aufrufen
        this.service.checkPrice(errorStack, priceParam, billItem);

        // Ergebnis prüfen
        assertEquals(0, errorStack.size());
    }

    @Test
    void testCheckPriceError() {
        // Beispieldaten erstellen
        List<String> errorStack = new ArrayList<String>();
        SpinJsonNode priceParam = JSON("{}");
        priceParam.prop("free_units", 5);
        priceParam.prop("cost_cap", -1);
        priceParam.prop("price_per_unit", 10.00);
        SpinJsonNode billItem = JSON("{}");
        billItem.prop("amount", 10);
        billItem.prop("price", 100.00);

        // Methode aufrufen
        this.service.checkPrice(errorStack, priceParam, billItem);

        // Ergebnis prüfen
        assertEquals(1, errorStack.size());
    }

    @Test
    void testCheckPriceWithCostCap() {
        // Beispieldaten erstellen
        List<String> errorStack = new ArrayList<String>();
        SpinJsonNode priceParam = JSON("{}");
        priceParam.prop("free_units", 5);
        priceParam.prop("cost_cap", 20);
        priceParam.prop("price_per_unit", 10.00);
        SpinJsonNode billItem = JSON("{}");
        billItem.prop("amount", 22);
        billItem.prop("price", 150.00);

        // Methode aufrufen
        this.service.checkPrice(errorStack, priceParam, billItem);

        // Ergebnis prüfen
        assertEquals(0, errorStack.size());
    }

    @Test
    void testCheckPriceWithCostCapError() {
        // Beispieldaten erstellen
        List<String> errorStack = new ArrayList<String>();
        SpinJsonNode priceParam = JSON("{}");
        priceParam.prop("free_units", 5);
        priceParam.prop("cost_cap", 20);
        priceParam.prop("price_per_unit", 10.00);
        SpinJsonNode billItem = JSON("{}");
        billItem.prop("amount", 22);
        billItem.prop("price", 220.00);

        // Methode aufrufen
        this.service.checkPrice(errorStack, priceParam, billItem);

        // Ergebnis prüfen
        assertEquals(1, errorStack.size());
    }
}