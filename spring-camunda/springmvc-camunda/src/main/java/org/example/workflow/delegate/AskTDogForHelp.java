package org.example.workflow.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * @author renc
 */
@Component
public class AskTDogForHelp implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String question = (String) execution.getVariable("question");
        execution.getProcessEngineServices().getRuntimeService()
                .createMessageCorrelation("AskTDog")
                .setVariable("question", question)
                .correlate();
    }
}
