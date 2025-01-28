package com.ml.training.gen.ai.service.sentiment;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.sentiment.model.Sentiment;
import org.springframework.lang.NonNull;

public interface SentimentService {

  boolean supports(@NonNull final ClientType clientType);

  Sentiment getSentiment(@NonNull final String message);

}
