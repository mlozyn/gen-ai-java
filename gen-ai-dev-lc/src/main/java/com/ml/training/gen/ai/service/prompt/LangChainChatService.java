package com.ml.training.gen.ai.service.prompt;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.springframework.lang.NonNull;

/*

  Sample of high level API usage
  Offers numerous advantages compared to the low-level approach, including simplicity,
  improved testability, and seamless chaining of calls

  watch this! https://www.youtube.com/watch?v=Bx2OpE1nj34

*/

public interface LangChainChatService {

  Result<String> ask(@MemoryId @NonNull final Long memoryId,
      @UserMessage @NonNull final String userMessage);

  @SystemMessage("{{system-message}}")
  Result<String> ask(@MemoryId @NonNull final Long memoryId,
      @NonNull @V("system-message") final String systemMessage,
      @UserMessage @NonNull final String userMessage);

}
