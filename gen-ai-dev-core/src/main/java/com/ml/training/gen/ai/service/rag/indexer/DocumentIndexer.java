package com.ml.training.gen.ai.service.rag.indexer;

import com.ml.training.gen.ai.service.common.model.ClientType;
import java.io.InputStream;
import org.springframework.lang.NonNull;

public interface DocumentIndexer {

  boolean supports(@NonNull final ClientType clientType);

  void execute(@NonNull final InputStream source);

}
