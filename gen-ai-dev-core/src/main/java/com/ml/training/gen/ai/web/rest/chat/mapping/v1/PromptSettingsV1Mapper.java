package com.ml.training.gen.ai.web.rest.chat.mapping.v1;

import com.ml.training.gen.ai.service.prompt.model.PromptSettings;
import com.ml.training.gen.ai.web.rest.chat.model.v1.PromptSettingsV1;
import java.util.Objects;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class PromptSettingsV1Mapper {

  public PromptSettings map(@Nullable final PromptSettingsV1 settings) {
    if (Objects.isNull(settings)) {
      return null;
    }

    return PromptSettings.builder()
        .withTemperature(settings.getTemperature())
        .withTopP(settings.getTopP())
        .withFrequencyPenalty(settings.getFrequencyPenalty())
        .withPresencePenalty(settings.getPresencePenalty())
        .withMaxTokens(settings.getMaxTokens())
        .build();

  }

}