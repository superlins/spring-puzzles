package org.example.workflow;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.test.DmnEngineRule;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author renc
 */
public class DMNTester {

    @Rule
    public DmnEngineRule dmnEngineRule = new DmnEngineRule();

    public DmnEngine dmnEngine;
    public DmnDecision decision;

    @Before
    public void parseDecision() {
        InputStream inputStream = DMNTester.class.getResourceAsStream("dish-decision.dmn11.xml");
        dmnEngine = dmnEngineRule.getDmnEngine();
        decision = dmnEngine.parseDecision("decision", inputStream);
    }

    @Test
    public void shouldServeDryAgedInSpringForFewGuests() {
        VariableMap variables = Variables
                .putValue("season", "Spring")
                .putValue("guestCount", 4);

        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);
        assertEquals("Dry Aged Gourmet Steak", result.getSingleResult().getSingleEntry());
    }
}
