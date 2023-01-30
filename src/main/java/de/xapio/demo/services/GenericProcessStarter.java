package de.xapio.demo.services;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.NumberValue;
import org.camunda.bpm.engine.variable.value.StringValue;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

import static org.camunda.spin.Spin.JSON;

@Service @Slf4j
public class GenericProcessStarter implements JavaDelegate {
    public final static String PAYLOAD = "payload";

    public final static String CALL_PROCESS = "call_process";

    private final RuntimeService runtimeService;

    public GenericProcessStarter(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    /**
     * process start via process
     *
     * @param delegate
     * @throws Exception
     */
    @Override
    public void execute(DelegateExecution delegate) throws Exception {

        // den 'process key' des aufzurufenden Prozesses holen
        String processKey = (String) delegate.getVariable(CALL_PROCESS);

        // get all vars
        Map<String, Object> variables = delegate.getVariables();
        variables.remove(CALL_PROCESS);

        log.info("Prozess " + processKey + " wird aufgerufen");
        ProcessInstance instance = this.runtimeService.createProcessInstanceByKey(processKey)
                .businessKey(delegate.getProcessBusinessKey())
                .setVariables(variables)
                .execute();
        log.info("Definition ID -> " + instance.getProcessDefinitionId());
    }

    /**
     * process start via api call
     *
     * @param payload
     */
    public void startProcess(String payload) {
        SpinJsonNode json = JSON(payload);

        String processId = "death_letter_process";
        // get process id
        if(json.hasProp("process_id")) {
            processId = json.prop("process_id").stringValue();
        }

        ProcessInstantiationBuilder ib = this.runtimeService.createProcessInstanceByKey(processId);

        // set business key
        if(json.hasProp("businessKey")) {
            ib.businessKey(json.prop("businessKey").stringValue());
        }

        // set variables
        if(json. hasProp("variables")) {
            SpinJsonNode variables = json.prop("variables");
            // add all variables to the instance
            variables.fieldNames().forEach(prop -> {
                this.checkTypes(ib, variables.prop(prop), prop);
            });
        }

        // execute process
        ProcessInstance instance = ib.execute();
        log.info(instance.getProcessDefinitionId());
    }

    public void checkTypes(ProcessInstantiationBuilder ib, SpinJsonNode var, String prop) {

        SpinJsonNode v = var.prop("value");

        // string
        if(this.is(var, "string")) {
            log.info("setting string variable -> " + prop);
            StringValue value = Variables.stringValue(v.stringValue());
            ib.setVariable(prop, value);
        }

        // number
        if(this.is(var, "number")) {
            log.info("setting number variable -> " + prop);
            NumberValue value = Variables.numberValue(v.numberValue());
            ib.setVariable(prop, value);
        }

        // array
        if(this.is(var, "array")) {
            log.info("setting array variable -> " + prop + " array? " + v.isArray());
            ArrayList<String> items = Lists.newArrayList();
            v.elements().forEach(item -> {
                items.add(item.stringValue());
            });
            ib.setVariable(prop, items);
        }
    }

    public boolean is(SpinJsonNode var, String type) {
        return var.prop("type").stringValue().equals(type.toLowerCase());
    }
}
