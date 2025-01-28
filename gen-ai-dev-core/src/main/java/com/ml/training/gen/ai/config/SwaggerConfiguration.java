package com.ml.training.gen.ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

  @Bean
  public OpenAPI genAIApi() {
    return new OpenAPI()

        .info(new Info().title("Generative AI APIs")
            .version("v1")
        );
  }

}