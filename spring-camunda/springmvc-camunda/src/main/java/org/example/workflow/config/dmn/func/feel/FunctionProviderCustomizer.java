package org.example.workflow.config.dmn.func.feel;

import org.camunda.bpm.dmn.feel.impl.scala.function.CustomFunction;
import org.camunda.bpm.dmn.feel.impl.scala.function.FeelCustomFunctionProvider;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author renc
 */
@Component
public class FunctionProviderCustomizer implements FeelCustomFunctionProvider {

    private final Map<String, CustomFunction> functions = new HashMap<>();

    public FunctionProviderCustomizer() {
        functions.put("datetime", CustomFunction.create()
                .setParams("v")
                .setFunction(args -> {
                    String val = (String) args.get(0);
                    return DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(val);
                })
                .build());
    }

    @Override
    public Optional<CustomFunction> resolveFunction(String functionName) {
        return Optional.ofNullable(functions.get(functionName));
    }

    @Override
    public Collection<String> getFunctionNames() {
        return functions.keySet();
    }

}
