package org.example.workflow.config.plugin;

import org.camunda.spin.impl.json.jackson.format.JacksonJsonDataFormat;
import org.camunda.spin.spi.DataFormatConfigurator;
import spinjar.com.jayway.jsonpath.Option;

import static spinjar.com.jayway.jsonpath.Configuration.defaultConfiguration;

public class CamundaJsonPathFormatConfiguration implements DataFormatConfigurator<JacksonJsonDataFormat> {

  @Override
  public Class<JacksonJsonDataFormat> getDataFormatClass() {
    return JacksonJsonDataFormat.class;
  }

  @Override
  public void configure(JacksonJsonDataFormat dataFormat) {
    dataFormat.setJsonPathConfiguration(defaultConfiguration()
            .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS));
  }
}
