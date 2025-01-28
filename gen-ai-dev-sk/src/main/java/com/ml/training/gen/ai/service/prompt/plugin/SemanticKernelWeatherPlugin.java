package com.ml.training.gen.ai.service.prompt.impl.sk.plugin;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SemanticKernelWeatherPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(SemanticKernelWeatherPlugin.class);

  @DefineKernelFunction(
      name = "get_weather",
      description = "Returns the weather forecast for tomorrow for a given city",
      returnDescription = "Weather forecast sourced from the weather service. Summarize it in up to two concise sentences.",
      returnType = "java.lang.String"
  )
  public String getWeather(
      @KernelFunctionParameter(name = "country", description = "The country in which the city is located", type = String.class)
      @NonNull final String country,
      @KernelFunctionParameter(name = "city", description = "The city for the weather forecast", type = String.class)
      @NonNull final String city) {
    LOG.info("Getting weather forecast for {}, {}", city, country);
    return String.format("""
          Weather Forecast for %s City
          
          Morning:
          Temperature: 12°C (54°F)
          Conditions: Partly cloudy with a light breeze.
          Wind: 8 km/h from the northwest.

          Afternoon:
          Temperature: 18°C (64°F)
          Conditions: Sunny with clear skies.
          Wind: 10 km/h from the west.

          Evening:
          Temperature: 14°C (57°F)
          Conditions: Cool and clear.
          Wind: 5 km/h from the southwest.

          Night:
          Temperature: 9°C (48°F)
          Conditions: Calm with light fog developing in low-lying areas.
          Wind: Minimal.

          Special Advisory:
          No precipitation expected today. A perfect day to enjoy outdoor activities!        
        """, city);
  }

}
