package org.example.workflow.listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.core.model.CoreModelElement;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author renc
 */
@Component
public class BpmnProcessListener {

    private final static Logger logger = LoggerFactory.getLogger(BpmnProcessListener.class);

    @EventListener(condition="#taskDelegate.eventName=='create'")
    public void onTaskEvent(DelegateTask taskDelegate) {
        // handle mutable task event
        logger.info("[taskDelegate] ExecutionId:{}, EventName: {}",
                taskDelegate.getExecutionId(),
                taskDelegate.getEventName());
    }

    @EventListener
    public void onExecutionEvent(DelegateExecution executionDelegate) {
        // handle mutable execution event

        String eventName = executionDelegate.getEventName();
        String currentActivityId = executionDelegate.getCurrentActivityId();

        // process event
        CoreModelElement coreActivity = ((ExecutionEntity) executionDelegate).getEventSource();
        if (coreActivity instanceof ProcessDefinitionEntity) {
            System.out.printf("execution [%s] '%s' event ..\n", eventName, coreActivity.getName());
            return;
        }

        // start/end event
        if (ExecutionListener.EVENTNAME_START.equals(eventName) || ExecutionListener.EVENTNAME_END.equals(eventName)) {
            System.out.printf("execution [%s] '%s' event\n", eventName, currentActivityId);
        }

        // take
        if (ExecutionListener.EVENTNAME_TAKE.equals(eventName)) {
            System.out.printf("execution [%s] '%s' event\n", eventName, coreActivity);
        }

        // logger.info("[executionDelegate] Id:{}, EventName: {}",
        //         executionDelegate.getId(),
        //         executionDelegate.getEventName());
    }

    // @EventListener
    // public void onHistoryEvent(HistoryEvent historyEvent) {
    //     // handle history event
    //     logger.info("[historyEvent] ExecutionId:{}, EventType: {}",
    //             historyEvent.getExecutionId(),
    //             historyEvent.getEventType());
    // }
}
