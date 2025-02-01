package com.ml.training.gen.ai.service.sentiment;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.sentiment.model.Sentiment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public class LangChainSentimentService implements SentimentService {

  private static final Logger LOG = LoggerFactory.getLogger(LangChainSentimentService.class);

  private final ClientType clientType;
  private final SentimentAnalyzer sentimentAnalyzer;

  public LangChainSentimentService(@NonNull final ClientType clientType,
      @NonNull final ChatLanguageModel languageModel) {
    this.clientType = clientType;
    this.sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, languageModel);
  }

  @Override
  public boolean supports(@NonNull final ClientType clientType) {
    return this.clientType == clientType;
  }

  @Override
  public Sentiment getSentiment(@NonNull final String message) {
    LOG.info("Sentiment execution STARTED Message: '{}'", message);
    final var stopWatch = StopWatch.createStarted();

    try {
      final Sentiment result = sentimentAnalyzer.sentimentOf(message);

      stopWatch.stop();
      LOG.info("Sentiment execution COMPLETED. Sentiment: '{}'. Took: {} ms",
          result.getTitle(), stopWatch.getTime(TimeUnit.MILLISECONDS));

      return result;
    } catch (final Exception exception) {
      LOG.error("Sentiment execution COMPLETED. Error: '{}'", exception.getMessage());
      throw exception;
    }
  }

  interface SentimentAnalyzer {

    // we must carefully adjust system & user message to prevent unexpected responses from model
    // as example response must be just one word, upper /lower case (not mixed case like Neutral)
    // otherwise it won't be mapped back to Sentiment class

    @SystemMessage("You are a helpful sentiment analysis AI assistant. Use upper case letters only in your answer.")
    @UserMessage("Analyze sentiment of the text in triple quotes. Answer with just one word. \"\"\"{{text}}\"\"\"")
    Sentiment sentimentOf(@V("text") final String text);
  }

}
