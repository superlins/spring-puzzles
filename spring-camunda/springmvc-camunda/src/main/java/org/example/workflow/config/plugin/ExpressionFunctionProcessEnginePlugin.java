package org.example.workflow.config.plugin;

import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.javax.el.FunctionMapper;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author renc
 */
@Component
public class ExpressionFunctionProcessEnginePlugin extends AbstractCamundaConfiguration {

    private final List<FunctionMapper> functionMappers;

    public ExpressionFunctionProcessEnginePlugin(List<FunctionMapper> functionMappers) {
        this.functionMappers = functionMappers;
    }

    @Override
    public void postInit(SpringProcessEngineConfiguration processEngineConfiguration) {
        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();
        functionMappers.forEach(expressionManager::addFunctionMapper);
    }
}
