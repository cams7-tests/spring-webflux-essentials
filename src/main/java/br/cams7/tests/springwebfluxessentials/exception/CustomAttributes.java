package br.cams7.tests.springwebfluxessentials.exception;

import java.util.Map;
import javax.validation.ConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CustomAttributes extends DefaultErrorAttributes {
  private static final String MESSAGE_ATTRIBUTE = "message";
  private static final String DEVELOPER_MESSAGE_ATTRIBUTE = "developerMessage";

  @Override
  public Map<String, Object> getErrorAttributes(
      ServerRequest request, ErrorAttributeOptions options) {
    var errorAttributes = super.getErrorAttributes(request, options);
    var throwable = getError(request);
    if (throwable instanceof ResponseStatusException) {
      var exception = (ResponseStatusException) throwable;
      errorAttributes.put(MESSAGE_ATTRIBUTE, exception.getLocalizedMessage());
      errorAttributes.put(DEVELOPER_MESSAGE_ATTRIBUTE, "A ResponseStatusException happened");
    } else if (throwable instanceof ConstraintViolationException) {
      var exception = (ConstraintViolationException) throwable;
      errorAttributes.put(MESSAGE_ATTRIBUTE, exception.getLocalizedMessage());
      errorAttributes.put(DEVELOPER_MESSAGE_ATTRIBUTE, "A ConstraintViolationException happened");
    } else if (throwable instanceof DataIntegrityViolationException) {
      var exception = (DataIntegrityViolationException) throwable;
      errorAttributes.put(MESSAGE_ATTRIBUTE, exception.getLocalizedMessage());
      errorAttributes.put(
          DEVELOPER_MESSAGE_ATTRIBUTE, "A DataIntegrityViolationException happened");
    }
    return errorAttributes;
  }
}
