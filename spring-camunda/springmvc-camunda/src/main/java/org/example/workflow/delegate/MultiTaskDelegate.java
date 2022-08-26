package org.example.workflow.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author renc
 */
@Component
public class MultiTaskDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Map<String, Object> variablesLocal = execution.getVariablesLocal();
        System.out.println("loopCounter" + variablesLocal.get("loopCounter"));
        System.out.println("nrOfInstances" + variablesLocal.get("nrOfInstances"));
        System.out.println("nrOfActiveInstances" + variablesLocal.get("nrOfActiveInstances"));
        System.out.println("nrOfCompletedInstances" + variablesLocal.get("nrOfCompletedInstances"));
    }
}
