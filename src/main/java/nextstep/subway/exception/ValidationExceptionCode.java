package nextstep.subway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ValidationExceptionCode implements ExceptionCode {

  CONTAINS_INVALID_FIELD (HttpStatus.BAD_REQUEST, "validation-1000", "허용하지 않는 값이 존재합니다."),
  ;

  private final HttpStatus httpStatus;
  private final String serviceErrorCode;
  private final String defaultErrorMessage;

  ValidationExceptionCode(HttpStatus httpStatus, String serviceErrorCode, String defaultErrorMessage) {
    this.httpStatus = httpStatus;
    this.serviceErrorCode =  serviceErrorCode;
    this.defaultErrorMessage = defaultErrorMessage;
  }

  @Override
  public HttpStatus getHttpStatusCode() {
    return httpStatus;
  }

  @Override
  public String getServiceErrorCode() {
    return serviceErrorCode;
  }

  @Override
  public String getDefaultErrorMessage() {
    return defaultErrorMessage;
  }
}
