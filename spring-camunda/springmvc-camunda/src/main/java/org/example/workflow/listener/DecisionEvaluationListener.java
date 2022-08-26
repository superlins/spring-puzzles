package org.example.workflow.listener;

import org.camunda.bpm.dmn.engine.delegate.DmnDecisionEvaluationListener;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationEvent;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationListener;
import org.camunda.bpm.dmn.engine.delegate.DmnEvaluatedDecisionRule;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnDecisionContext;
import org.camunda.bpm.dmn.engine.impl.evaluation.DecisionTableEvaluationHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author renc
 * @see DmnDecisionEvaluationListener
 * @see DmnDecisionTableEvaluationListener
 * @see DefaultDmnDecisionContext#generateDecisionEvaluationEvent#L183
 * @see DecisionTableEvaluationHandler#evaluate#L87
 */
@Component
public class DecisionEvaluationListener implements DmnDecisionTableEvaluationListener {

    @Override
    public void notify(DmnDecisionTableEvaluationEvent evaluationEvent) {
        List<DmnEvaluatedDecisionRule> matchingRules = evaluationEvent.getMatchingRules();
        System.out.println(">>>>>> matching rules: " + matchingRules);
    }
}
