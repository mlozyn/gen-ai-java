package com.ml.training.gen.ai.web.rest.common.error.logger;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ml.training.gen.ai.utils.JsonUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class RestErrorResponseLogger extends OncePerRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(RestErrorResponseLogger.class);

  static final List<MediaType> TEXT_CONTENT_TYPES = Arrays.asList(
      MediaType.valueOf("text/*"),
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML,
      MediaType.valueOf("application/*+json"),
      MediaType.valueOf("application/*+xml")
  );

  static final List<MediaType> FORM_CONTENT_TYPES = Arrays.asList(
      MediaType.APPLICATION_FORM_URLENCODED,
      MediaType.MULTIPART_FORM_DATA
  );

  @Override
  protected void doFilterInternal(final HttpServletRequest request,
      final HttpServletResponse response, final FilterChain filterChain)
      throws ServletException, IOException {
    if (isAsyncDispatch(request)) {
      filterChain.doFilter(request, response);
    } else {
      doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
    }
  }

  private void doFilterWrapped(final ContentCachingRequestWrapper request,
      final ContentCachingResponseWrapper response,
      final FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } finally {
      try {
        logResponse(request, response);
      } catch (final Exception ex) {
        LOG.debug("Unable to log the response. Error: '{}'", ex.getMessage());
      }
      response.copyBodyToResponse();
    }
  }

  private String logErrorResponse(final ContentCachingRequestWrapper request,
      final ContentCachingResponseWrapper response) {

    final ObjectNode root = JsonUtils.OBJECT_MAPPER.createObjectNode();

    final ObjectNode requestNode = root.putObject("request");
    processRequestMethod(request, requestNode);
    processRequestHeader(request, requestNode);
    processRequestBody(request, requestNode);

    final ObjectNode responseNode = root.putObject("response");
    processResponse(response, responseNode);

    return JsonUtils.toJsonString(root);
  }

  private void processRequestMethod(final ContentCachingRequestWrapper request,
      final ObjectNode parent) {
    final String queryString = request.getQueryString();

    final String endpointUrl;
    if (queryString == null) {
      endpointUrl = request.getRequestURI();
    } else {
      endpointUrl = request.getRequestURI() + '?' + queryString;
    }

    parent.put("method", request.getMethod() + " " + endpointUrl);
  }

  private void processRequestHeader(final ContentCachingRequestWrapper request,
      final ObjectNode parent) {
    final ObjectNode headersRoot = parent.putObject("headers");

    Collections.list(request.getHeaderNames()).forEach(headerName -> {
      final Enumeration<String> values = request.getHeaders(headerName);
      if (Objects.isNull(values)) {
        return;
      }

      final ArrayNode headerNode = headersRoot.putArray(headerName);
      values.asIterator().forEachRemaining(headerNode::add);
    });
  }

  private void processRequestBody(final ContentCachingRequestWrapper request,
      final ObjectNode parent) {
    final String method = request.getMethod();

    // body could be just for PATCH, POST, PUT
    final boolean hasBody = HttpMethod.PATCH.name().equalsIgnoreCase(method)
        || HttpMethod.POST.name().equalsIgnoreCase(method)
        || HttpMethod.PUT.name().equalsIgnoreCase(method);

    if (!hasBody) {
      return;
    }

    final String bodyPropertyName = "body";
    final ObjectNode bodyRoot = parent.putObject(bodyPropertyName);

    try {
      final Map<String, String[]> params = request.getParameterMap();
      processParameters(params, request.getContentType(), bodyRoot);

      final byte[] content = request.getContentAsByteArray();
      processContent(content, request.getContentType(), request.getCharacterEncoding(), bodyRoot);
    } catch (final Exception exception) {
      LOG.debug("Unable to get request body, skipping");
    }

    if (bodyRoot.isEmpty()) {
      parent.remove(bodyPropertyName);
    }
  }

  private void processResponse(final ContentCachingResponseWrapper response,
      final ObjectNode parent) {

    final int status = response.getStatus();

    parent.put("status", status);
    parent.put("reason phrase", HttpStatus.valueOf(status).getReasonPhrase());

    final String bodyPropertyName = "body";
    final ObjectNode bodyRoot = parent.putObject(bodyPropertyName);

    final byte[] content = response.getContentAsByteArray();
    processContent(content, response.getContentType(), response.getCharacterEncoding(), bodyRoot);

    if (bodyRoot.isEmpty()) {
      parent.remove(bodyPropertyName);
    }
  }

  private void processContent(final byte[] content, final String contentType,
      final String contentEncoding, final ObjectNode parent) {
    if (Objects.isNull(content) || content.length == 0 || !StringUtils.hasText(contentType)) {
      return;
    }

    final MediaType mediaType = MediaType.valueOf(contentType);
    final boolean isTextContent = TEXT_CONTENT_TYPES.stream().anyMatch(
        visibleType -> visibleType.includes(mediaType));

    if (!isTextContent) {
      return;
    }

    try {
      final String contentString = new String(content, contentEncoding);
      parent.put(
          "content",
          contentString.replaceAll("\r\n|\r|\n", " ")
      );
    } catch (final UnsupportedEncodingException ex) {
      parent.put(
          "content",
          String.format("%d bytes content", content.length)
      );
    }
  }

  private void processParameters(final Map<String, String[]> parameters, final String contentType,
      final ObjectNode parent) {
    if (Objects.isNull(parameters) || parameters.isEmpty() || StringUtils.hasText(contentType)) {
      return;
    }

    final MediaType mediaType = MediaType.valueOf(contentType);
    final boolean isParametersContent = FORM_CONTENT_TYPES.stream().anyMatch(
        visibleType -> visibleType.includes(mediaType));

    if (!isParametersContent) {
      return;
    }

    final ObjectNode parametersNode = parent.putObject("params");
    parameters.forEach((parameterName, parameterValues) -> {
      final ArrayNode parameterNode = parametersNode.putArray(parameterName);
      for (final String parameterValue : parameterValues) {
        parameterNode.add(parameterValue);
      }
    });
  }

  private void logResponse(final ContentCachingRequestWrapper request,
      final ContentCachingResponseWrapper response) {
    final int status = response.getStatus();
    if ((status >= HttpStatus.OK.value()) && (status < HttpStatus.BAD_REQUEST.value())) {
      return;
    }

    LOG.error(logErrorResponse(request, response));
  }

  private ContentCachingRequestWrapper wrapRequest(final HttpServletRequest request) {
    if (request instanceof ContentCachingRequestWrapper) {
      return (ContentCachingRequestWrapper) request;
    } else {
      return new ContentCachingRequestWrapper(request);
    }
  }

  private ContentCachingResponseWrapper wrapResponse(final HttpServletResponse response) {
    if (response instanceof ContentCachingResponseWrapper) {
      return (ContentCachingResponseWrapper) response;
    } else {
      return new ContentCachingResponseWrapper(response);
    }
  }

}
