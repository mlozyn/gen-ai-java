package com.ml.training.gen.ai.web.rest.common.error;

import com.ml.training.gen.ai.service.chat.error.ChatNotFoundException;
import com.ml.training.gen.ai.web.rest.common.error.exception.UnsupportedClientType;
import com.ml.training.gen.ai.web.rest.common.error.model.RestEndpointError;
import jakarta.validation.ValidationException;
import java.time.Instant;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestErrorController {

  private static final Logger LOG = LoggerFactory.getLogger(RestErrorController.class);

  private static final String VALIDATION_ERROR_TITLE = "The request does not follow the correct syntax";
  private static final String RESOURCE_NOT_FOUND_PATTERN = "Resource not found for '%s %s'";

  @ExceptionHandler({ChatNotFoundException.class})
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ResponseEntity<?> handleChatNotFoundException(final ChatNotFoundException exception) {
    return createResponseEntity(HttpStatus.NOT_FOUND, "Not found", exception.getMessage());
  }

  @ExceptionHandler({UnsupportedClientType.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<?> handleUnsupportedClientType(final UnsupportedClientType exception) {
    return createResponseEntity(HttpStatus.BAD_REQUEST, "Service not found",
        exception.getMessage());
  }

  @ExceptionHandler({NoResourceFoundException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<?> handleAccessDeniedException(final NoResourceFoundException exception) {
    String message = String.format(RESOURCE_NOT_FOUND_PATTERN, exception.getHttpMethod(),
        exception.getResourcePath());

    return createResponseEntity(HttpStatus.BAD_REQUEST, "Resource not found", message);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<?> handleNoHandlerFoundException(final NoHandlerFoundException exception) {
    String message = String.format(RESOURCE_NOT_FOUND_PATTERN, exception.getHttpMethod(),
        exception.getRequestURL());

    return createResponseEntity(HttpStatus.BAD_REQUEST, "Resource not found", message);
  }

  @ExceptionHandler(BindException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<?> handleBindException(final Errors exception) {
    final String message = exception.getAllErrors().stream()
        .map(this::getObjectErrorMessage)
        .collect(Collectors.joining("; "));

    return createResponseEntity(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_TITLE, message);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<?> handleMethodArgumentTypeMismatchException(
      final MethodArgumentTypeMismatchException exception) {
    String message = String.format("Invalid url parameter '%s' has been sent. %s",
        exception.getName(), exception.getMessage());

    return createResponseEntity(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_TITLE, message);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<?> handleMethodArgumentNotValidException(
      final MethodArgumentNotValidException exception) {
    BindingResult bindingResult = exception.getBindingResult();
    String message = bindingResult.getAllErrors().stream()
        .map(this::getObjectErrorMessage)
        .collect(Collectors.joining("; "));

    return createResponseEntity(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_TITLE, message);
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<?> handleValidationException(final ValidationException exception) {
    return createResponseEntity(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_TITLE,
        exception.getMessage());
  }

  @ExceptionHandler(ServletRequestBindingException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<?> handleServletRequestBindingException(
      final ServletRequestBindingException exception) {
    return createResponseEntity(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_TITLE,
        exception.getMessage());
  }

  @ExceptionHandler({
      HttpMessageNotReadableException.class,
      HttpMediaTypeNotAcceptableException.class,
      HttpMediaTypeNotSupportedException.class,
      HttpRequestMethodNotSupportedException.class
  })
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<?> handleHttpMessageException(final Exception exception) {
    return createResponseEntity(HttpStatus.BAD_REQUEST, "Bad request",
        exception.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<?> handleDefaultException(final Exception exception) {
    return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error",
        exception.getMessage());
  }

  protected ResponseEntity<?> createResponseEntity(
      final HttpStatus status, final String title, final String detail) {
    LOG.error("An error '{}' with status code '{}' occurred while processing the request",
        detail, status.value());

    final RestEndpointError result = RestEndpointError.builder()
        .withTitle(title)
        .withDetail(detail)
        .withError(status.getReasonPhrase())

        .withTimestamp(Instant.now())

        .build();

    return ResponseEntity.status(status).body(result);
  }

  private String getObjectErrorMessage(final ObjectError error) {
    if (error instanceof FieldError fieldError) {
      final String fieldName = fieldError.getField();
      return String.join(": ", fieldName, fieldError.getDefaultMessage());
    } else {
      return String.join(": ", error.getObjectName(), error.getDefaultMessage());
    }
  }

}
