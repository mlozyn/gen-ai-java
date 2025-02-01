package com.ml.training.gen.ai.service.rag.indexer.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

class PDFDocumentIndexerTest {

  private static final String PDF_DOCUMENT_FLP = "data/rag/role-library.pdf";

  private EmbeddingService embeddingServiceMock;

  private PDFDocumentIndexer indexer;

  @BeforeEach
  public void setUp() {
    this.embeddingServiceMock = Mockito.mock(EmbeddingService.class);
    this.indexer = new PDFDocumentIndexer(embeddingServiceMock);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void executeWorks() throws IOException {
    final var resource = new ClassPathResource(PDF_DOCUMENT_FLP);

    indexer.execute(resource.getInputStream());

    final ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
    verify(embeddingServiceMock, times(1)).save(captor.capture());

    final List<String> values = captor.getValue();
    assertEquals(32, values.size());
  }

}