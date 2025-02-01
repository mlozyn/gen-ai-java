package com.ml.training.gen.ai.service.prompt.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

/*
  Created a separate project dedicated to exploring LangChain4j function execution.

  watch this!!! https://www.youtube.com/watch?v=cjI_6Siry-s
 */

public class WeatherTools {

  private static final Logger LOG = LoggerFactory.getLogger(WeatherTools.class);

  @Tool("Weather forecast sourced from the weather service. Summarize it in up to two concise sentences.")
  public String getWeather(
      @P("The country in which the city is located")
      @NonNull final String country,
      @P("The city for the weather forecast")
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
