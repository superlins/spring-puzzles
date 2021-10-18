package org.example.workflow.delegate;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * @author renc
 */
@Slf4j
@Component
public class LoggerDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processDefinitionId = execution.getProcessDefinitionId();
        String processBusinessKey = execution.getProcessBusinessKey();
        String processInstanceId = execution.getProcessInstanceId();
        String currentActivityId = execution.getCurrentActivityId();
        String currentActivityName = execution.getCurrentActivityName();
        String businessKey = execution.getBusinessKey();
        log.info("LoggerDelegate invoked by " +
                        "processDefinitionId: {}, " +
                        "processBusinessKey: {}, " +
                        "processInstanceId: {}, " +
                        "currentActivityId: {}, " +
                        "businessKey: {}, " +
                        "currentActivityName: {}",
                processDefinitionId, processBusinessKey, processInstanceId, currentActivityId, businessKey, currentActivityName);
    }
}
