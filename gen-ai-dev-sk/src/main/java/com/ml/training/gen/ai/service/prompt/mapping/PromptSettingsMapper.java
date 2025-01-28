package com.ml.training.gen.ai.service.prompt.impl.sk.mapping;

import com.ml.training.gen.ai.service.prompt.model.PromptSettings;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PromptSettingsMapper {

  public PromptExecutionSettings map(final PromptSettings source) {
    final var builder = PromptExecutionSettings.builder();

    Optional.ofNullable(source.getTemperature()).ifPresent(builder::withTemperature);
    Optional.ofNullable(source.getTopP()).ifPresent(builder::withTopP);
    Optional.ofNullable(source.getPresencePenalty()).ifPresent(builder::withPresencePenalty);
    Optional.ofNullable(source.getFrequencyPenalty()).ifPresent(builder::withFrequencyPenalty);
    Optional.ofNullable(source.getMaxTokens()).ifPresent(builder::withMaxTokens);

    return builder.build();
  }

}
