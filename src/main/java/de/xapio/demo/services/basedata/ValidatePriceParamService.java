package de.xapio.demo.services.basedata;

import com.google.common.collect.Lists;
import de.xapio.demo.processes.GenericBillingVars;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.SpinList;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class ValidatePriceParamService extends AbstractBasedataService {

    @Value("${directus.url.type}")
    private String typeUrl;
    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        JacksonJsonNode contract = (JacksonJsonNode) delegate.getVariable(GenericBillingVars.CONTRACT);
        String billItemString = (String) delegate.getVariable(GenericBillingVars.BILL_ITEM);

        SpinJsonNode billItem = JSON(billItemString);

        // HACK!!! types should be globally available
        String typeString = this.restTemplate.getForObject(this.typeUrl + billItem.prop("type").toString(), String.class);
        SpinJsonNode type = JSON(typeString);
        String prefix = type.prop("data").prop("prefix").stringValue();

        // get price params
        SpinList<SpinJsonNode> priceParams = contract.jsonPath("$.price_params").elementList();
        List<SpinJsonNode> pps = priceParams.stream().filter(p -> p.jsonPath("$.price_param_id.type.prefix").stringValue().equals(prefix)).collect(Collectors.toList());

        // get quantity params
        SpinList<SpinJsonNode> quantityParams = contract.jsonPath("$.quantity_params").elementList();
        List<SpinJsonNode> qps = quantityParams.stream().filter(p -> p.jsonPath("$.type.prefix").stringValue().equals(prefix)).collect(Collectors.toList());

        String name = "";
        String message = "";
        String msgType = "";

        if(pps.size()==1){
            // this is the relevant price param
            SpinJsonNode priceParam = pps.get(0);

            // check, if there is a quantity param as well
            SpinJsonNode quantityParam = null;
            // TODO - wäre es fachlich sinnvoll, dass hier mehr als ein Wert pro Typ steht? Mir fällt kein Fall ein...
            if(qps.size()==1) {
                quantityParam = qps.get(0);
            }

            // create message
            List<String> errorStack = this.check(priceParam, quantityParam, billItem);
            if(errorStack.isEmpty()) {
                name = String.format("%s: Prüfung erfolgreich", type.prop("name").toString());
                message = String.format("Bei der Prüfung des Rechnungsposten '%s' konnten keine Fehler festgestellt werden.");
                msgType = "Korrekt geprüft";
            } else {
                name = String.format("%s: Prüfung nicht erfolgreich", type.prop("name").toString());
                StringBuilder sb = new StringBuilder(String.format("Bei der Prüfung des Rechnungsposten '%s' wurden Fehler festgestellt: \n"));
                errorStack.stream().forEach(e -> sb.append(e).append("\n"));
                message = sb.toString();
                msgType = "Fehler";
            }

        } else if (pps.isEmpty()) {
            // Fehler erzeugen -> kann ein Fehler in der Rechnung sein, oder aber in der Leistungskonfiguration
            name = String.format("%s: Keine Prüfung möglich", type.prop("name").toString());
            message = String.format("Zum Rechnungsposten Typ '%s' konnte keine Prüfung durchgeführt werden, da es " +
                    "keinen Preis Parameter des Typs im Vertrag gibt. Entweder ist hier der Vertrag (und evtl. sogar" +
                    " die Konfiguration der Leistung) falsch, oder der übermittelte Rechnungsdatensatz.", type.prop("name").toString());
            msgType = "Fehler";
        } else if (pps.size() > 1) {
            // Fehler erzeugen -> muss ein Fehler im Vertrag sein
            name = String.format("%s: Keine Prüfung möglich", type.prop("name").toString());
            message = String.format("Zum Rechnungsposten Typ '%s' konnte keine Prüfung durchgeführt werden, da es " +
                    "mehr als einen Preis Parameter des Typs im Vertrag gibt. Das sollte fachlich nicht möglich sein.", type.prop("name").toString());
            msgType = "Fehler";
        }
        // Variablen an den Prozess zurück geben
        this.sendVariables(delegate, name, message, msgType);
    }

    public void sendVariables(DelegateExecution delegate, String name, String message, String msgType) {
        delegate.setVariable(GenericBillingVars.VALIDATION_NAME, name);
        delegate.setVariable(GenericBillingVars.VALIDATION_MESSAGE, message);
        delegate.setVariable(GenericBillingVars.VALIDATION_RESULT, msgType);
    }

    public List<String> check(SpinJsonNode priceParam, SpinJsonNode quantityParam, SpinJsonNode billItem) {
        List<String> errorStack = Lists.newArrayList();

        this.checkQuantityParam(errorStack, quantityParam, billItem);
        this.checkPrice(errorStack, priceParam, billItem);

        return errorStack;
    }

    /**
     * Wenn es einen Quantity Param gibt, dann ist dieser bindend. D.h. der Rechnungsposten muss dieselbe
     * Anzahl an Einheiten haben.
     *
     * @param errorStack
     * @param quantityParam
     * @param billItem
     */
    public void checkQuantityParam(List<String> errorStack, SpinJsonNode quantityParam, SpinJsonNode billItem) {
        if(quantityParam != null) {
            BigDecimal bAmount = new BigDecimal(billItem.prop("amount").toString());
            BigDecimal qAmount = new BigDecimal(quantityParam.prop("amount").toString());
            if(bAmount.compareTo(qAmount) != 0) {
                errorStack.add("Die Anzahl der Einheiten im Rechnungsposten stimmt nicht mit der vertraglich vereinbarten fixen Menge überein.");
            }
        }
    }

    /**
     * Überprüft den Preis in möglichst vielen Konstellationen.
     * @param errorStack
     * @param priceParam
     * @param billItem
     */
    public void checkPrice(List<String> errorStack, SpinJsonNode priceParam, SpinJsonNode billItem) {
        // price param vars
        BigDecimal freeUnits = new BigDecimal(priceParam.prop("free_units").toString());
        BigDecimal costCap = new BigDecimal(priceParam.prop("cost_cap").toString());
        BigDecimal pricePerUnit = new BigDecimal(priceParam.prop("price_per_unit").toString());
        // bill item vars
        BigDecimal amount = new BigDecimal(billItem.prop("amount").toString());
        BigDecimal price = new BigDecimal(billItem.prop("price").toString());

        // verrechenbare Einheiten errechnen
        BigDecimal billableAmount = amount.subtract(freeUnits);
        // gibt es keine Verbrauchsobergrenze, so ist dieser Wert -1
        if(costCap.doubleValue() > -1) {
            // Es gibt eine Verbrauchsobergrenze, also wird überprüft, ob diese überschritten wird.
            if(costCap.subtract(freeUnits).compareTo(billableAmount) < 0) {
                // es wird überschritten also sind die verrechenbaren Einheiten Obergrenze - freie Einheiten
                billableAmount = costCap.subtract(freeUnits);
            }
        }

        // Preis errechen
        BigDecimal calculatedPrice = billableAmount.multiply(pricePerUnit);

        // Preise vergleichen
        if(price.compareTo(calculatedPrice) != 0) {
            // Preis ist ungleich
            errorStack.add(String.format("Der errechnete Preis (%s€) weicht vom Rechnungspreis (%s€) ab. Der" +
                    " Vergleichspreis wurde mit %s verrechenbaren Einheiten, bei einem Preis/Einheit von " +
                    "%s€ berechnet.",calculatedPrice.toString(), price.toString(), billableAmount.toString(), pricePerUnit.toString()));
        }
    }
}
