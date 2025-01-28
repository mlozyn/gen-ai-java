package com.ml.training.gen.ai.web.rest.common.error.exception;

import java.io.Serial;

public class UnsupportedClientType extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -6302155562049260631L;

  public UnsupportedClientType(final String message) {
    super(message);
  }

}
