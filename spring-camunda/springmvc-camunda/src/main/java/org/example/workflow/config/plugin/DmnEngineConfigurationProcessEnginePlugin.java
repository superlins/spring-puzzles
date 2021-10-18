package org.example.workflow.config.plugin;

import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionEvaluationEvent;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationEvent;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.dmn.feel.impl.scala.function.FeelCustomFunctionProvider;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration;
import org.camunda.bpm.spring.boot.starter.event.EventPublisherPlugin;
import org.camunda.bpm.spring.boot.starter.spin.SpringBootSpinProcessEnginePlugin;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author renc
 * @see SpringBootSpinProcessEnginePlugin
 * @see EventPublisherPlugin
 */
@Component
public class DmnEngineConfigurationProcessEnginePlugin extends AbstractCamundaConfiguration {

    private final List<FeelCustomFunctionProvider> feelCustomFunctionProviders;

    public DmnEngineConfigurationProcessEnginePlugin(List<FeelCustomFunctionProvider> feelCustomFunctionProviders) {
        this.feelCustomFunctionProviders = feelCustomFunctionProviders;
    }

    @Override
    public void preInit(SpringProcessEngineConfiguration processEngineConfiguration) {
        DefaultDmnEngineConfiguration dmnEngineConfiguration =
                (DefaultDmnEngineConfiguration) DmnEngineConfiguration.createDefaultDmnEngineConfiguration();
        dmnEngineConfiguration.setFeelCustomFunctionProviders(feelCustomFunctionProviders);
        processEngineConfiguration.setDmnEngineConfiguration(dmnEngineConfiguration);
    }

    @Override
    public void postInit(SpringProcessEngineConfiguration processEngineConfiguration) {
        DefaultDmnEngineConfiguration dmnEngineConfiguration = processEngineConfiguration.getDmnEngineConfiguration();

        dmnEngineConfiguration.getDecisionEvaluationListeners()
                .add((DmnDecisionEvaluationEvent evaluationEvent) -> System.out.println("DecisionEvaluationListener: " + evaluationEvent));
        dmnEngineConfiguration.getDecisionTableEvaluationListeners()
                .add((DmnDecisionTableEvaluationEvent evaluationEvent) -> System.out.println("DecisionTableEvaluationListener: " + evaluationEvent));
    }
}
