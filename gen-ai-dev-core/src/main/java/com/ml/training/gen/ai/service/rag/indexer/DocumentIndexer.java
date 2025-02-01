package com.ml.training.gen.ai.service.rag.indexer;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.NonNull;

public interface DocumentIndexer {

  void execute(@NonNull final InputStream source) throws IOException;

}
