package de.xapio.demo.services.basedata;

import com.google.common.collect.Lists;
import de.xapio.demo.processes.GenericBillingVars;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class ValidateBillService extends AbstractBasedataService {
    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // get vars
        JacksonJsonNode bill = (JacksonJsonNode) delegate.getVariable(GenericBillingVars.BILL);
        List<String> billItems = (List<String>) delegate.getVariable(GenericBillingVars.BILL_ITEMS);
        String priceGross = delegate.getVariable(GenericBillingVars.PRICE_GROSS).toString();
        String priceNet = delegate.getVariable(GenericBillingVars.PRICE_NET).toString();
        String priceTax = delegate.getVariable(GenericBillingVars.PRICE_TAX).toString();

        // collect errors
        List<String> errorStack = Lists.newArrayList();
        this.checkNet(errorStack, billItems, priceNet);
        this.checkGross(errorStack,priceNet, priceTax, priceGross );
        this.checkTax(errorStack, priceNet, priceTax);

        // Hack - copy & paste
        String name = "";
        String message = "";
        String msgType = "";

        if(errorStack.isEmpty()) {
            name = String.format("Rechnungsbeträge: Prüfung erfolgreich");
            message = String.format("Bei der Prüfung der Rechnungsbeträge (Brutto, Netto, Steuer) konnten keine Fehler festgestellt werden.");
            msgType = "Korrekt geprüft";
        } else {
            name = String.format("Rechnungsbeträge: Prüfung nicht erfolgreich");
            StringBuilder sb = new StringBuilder(String.format("Bei der Prüfung der Rechnungsbeträge (Brutto, Netto, Steuer) konnten folgende Fehler festgestellt werden:"));
            errorStack.stream().forEach(e -> sb.append(e));
            message = sb.toString();
            msgType = "Fehler";
        }

        this.sendVariables(delegate, name, message, msgType);
    }

    public void sendVariables(DelegateExecution delegate, String name, String message, String msgType) {
        delegate.setVariable(GenericBillingVars.VALIDATION_NAME, name);
        delegate.setVariable(GenericBillingVars.VALIDATION_MESSAGE, message);
        delegate.setVariable(GenericBillingVars.VALIDATION_RESULT, msgType);
    }

    public void checkNet(List<String> errorStack, List<String> billItems, String priceNet) {
        // calculate the net price
        BigDecimal calculatedNetPrice = billItems.stream()
                .map(i -> {
                    SpinJsonNode json = JSON(i);
                    String s = json.jsonPath("$.price").stringValue();
                    return s;
                })
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
        BigDecimal netPrice = new BigDecimal(priceNet);
        if(calculatedNetPrice.compareTo(netPrice) != 0) {
            errorStack.add(String.format("Der errechnete netto Preis (%s) weicht vom netto Preis der Provider Rechnung (%s) ab.", calculatedNetPrice.toPlainString(), priceNet));
        }
    }

    public void checkGross(List<String> errorStack, String priceNet, String priceTax, String priceGross ) {
        BigDecimal calculatedGross = new BigDecimal(priceNet).add(new BigDecimal(priceTax));
        if(calculatedGross.compareTo(new BigDecimal(priceGross)) != 0) {
            errorStack.add(String.format("Der errechnete brutto Preis (%s) weicht vom brutto Preis der Provider Rechnung (%s) ab.", calculatedGross.toPlainString(), priceGross));
        }
    }
    public void checkTax(List<String> errorStack, String priceNet, String priceTax) {
        // TODO - HACK der Steuersatz sollte dynamisch aus der DB kommen. Der gehört für mich an die Price Params
        BigDecimal ust = new BigDecimal("0.19");
        BigDecimal calculatedTax = ust.multiply(new BigDecimal(priceNet));
        if(calculatedTax.setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(priceTax)) != 0) {
            errorStack.add(String.format("Die errechnete USt (%s) weicht von der Steuer der Provider Rechnung (%s) ab.", calculatedTax.toPlainString(), priceTax));
        }
    }
}
