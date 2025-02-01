package com.ml.training.gen.ai.service.rag.indexer;

import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

// alternative way how to parse document
public class LangChainDocumentIndexer implements DocumentIndexer {

  private static final Logger LOG = LoggerFactory.getLogger(LangChainDocumentIndexer.class);

  private static final int MAX_SEGMENT_SIZE = 2000;
  private static final int MAX_OVERLAP_SIZE = 500;

  private final DocumentSplitter documentSplitter;
  private final EmbeddingService embeddingService;

  public LangChainDocumentIndexer(@NonNull final EmbeddingService embeddingService) {
    this.documentSplitter = DocumentSplitters.recursive(MAX_SEGMENT_SIZE, MAX_OVERLAP_SIZE);
    this.embeddingService = embeddingService;
  }

  @Override
  public void execute(@NonNull final InputStream source) {
    final StopWatch watch = StopWatch.createStarted();
    LOG.info("Document indexing STARTED");

    final List<TextSegment> segments = splitDocument(source);
    if (CollectionUtils.isEmpty(segments)) {
      return;
    }

    save(segments);

    watch.stop();
    LOG.info("Document indexing COMPLETED. Created '{}' segments. Took: {} ms", segments.size(),
        watch.getTime(TimeUnit.MILLISECONDS));
  }

  private void save(final List<TextSegment> segments) {
    final int batchSize = 10;
    final int batchCount = (segments.size() + batchSize - 1) / batchSize;
    for (int index = 0; index < batchCount; index++) {
      final var batch = segments.subList(
          index * batchSize,
          Math.min((index + 1) * batchSize, segments.size())
      );

      embeddingService.save(
          batch.stream()
              .map(TextSegment::text)
              .toList()
      );
    }
  }

  // simplest way to index the document, no reason to write own one
  private List<TextSegment> splitDocument(final InputStream source) {
    final var parser = new ApachePdfBoxDocumentParser();
    final Document document = parser.parse(source);

    return documentSplitter.split(document);
  }

}
