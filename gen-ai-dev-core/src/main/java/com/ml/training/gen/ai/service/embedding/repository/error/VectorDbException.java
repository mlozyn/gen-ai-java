package com.ml.training.gen.ai.service.embedding.repository.error;

import java.io.Serial;

public class VectorDbException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 3852019254307526249L;

  public VectorDbException(final String message) {
    super(message);
  }

  public VectorDbException(final String message, final Throwable exception) {
    super(message, exception);
  }

}
