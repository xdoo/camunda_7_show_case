package de.xapio.demo.services.basedata.exceptions;

import org.camunda.bpm.engine.ProcessEngineException;

public class RefNotFoundException extends ProcessEngineException {

    public RefNotFoundException(String message, int code) {
        super(message, code);
    }

}
