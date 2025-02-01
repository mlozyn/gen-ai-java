package com.ml.training.gen.ai.service.rag.indexer.pdf;

import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import com.ml.training.gen.ai.service.rag.indexer.DocumentIndexer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public class PDFDocumentIndexer implements DocumentIndexer {

  private static final Logger LOG = LoggerFactory.getLogger(PDFDocumentIndexer.class);

  private final EmbeddingService embeddingService;

  public PDFDocumentIndexer(@NonNull final EmbeddingService embeddingService) {
    this.embeddingService = embeddingService;
  }

  @Override
  public void execute(@NonNull final InputStream source) throws IOException {
    LOG.info("Document indexing STARTED");

    final String[] articles = splitDocument(source);
    embeddingService.save(Arrays.asList(articles));

    LOG.info("Document indexing COMPLETED. Articles count: {}", articles.length);
  }

  private String[] splitDocument(@NonNull final InputStream source) throws IOException {
    try (PDDocument document = Loader.loadPDF(source.readAllBytes())) {
      final var stripper = new PDFTextRAGStripper();

      final String text = stripper.getText(document);
      return text.split(PDFTextRAGStripper.ARTICLE_DELIMITER);
    }
  }

}
