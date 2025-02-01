package com.ml.training.gen.ai.service.rag.indexer.pdf;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/*
  Simple PDF document splitter for RAG.

  For RAG, splitting the document based on articles is beneficial,
  as it enables retrieving relevant articles from a vector database for a given query
  while also supplying related context in the LM prompt.

  This implementation assumes that articles are short and each one begins with bold text.
*/

public class PDFTextRAGStripper extends PDFTextStripper {

  public static final String ARTICLE_DELIMITER = "\n\n---\n\n";

  private final List<String> articles = new LinkedList<>();

  private StringBuilder current = new StringBuilder();
  private boolean isBoldSection = false;

  @Override
  protected void writeString(final String text, final List<TextPosition> textPositions)
      throws IOException {
    // Check if text is bold (based on font weight)
    final boolean isBold = textPositions.stream()
        .anyMatch(tp -> tp.getFont().getName().toLowerCase().contains("bold"));

    if (isBold) {
      if (addCurrentArticle()) {
        current = new StringBuilder();
      }

      isBoldSection = true;
    }

    if (isBoldSection || !text.isBlank()) {
      current.append(text);
    }
  }

  @Override
  public String getText(final PDDocument doc) throws IOException {
    super.getText(doc);

    addCurrentArticle();

    return String.join(ARTICLE_DELIMITER, articles);
  }

  private boolean addCurrentArticle() {
    final String article = current.toString().strip();
    final boolean result = !article.isBlank();

    if (result) {
      articles.add(article);
    }

    return result;
  }

}
