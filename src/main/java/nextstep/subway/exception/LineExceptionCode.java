package nextstep.subway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LineExceptionCode implements ExceptionCode {

  LINE_NOT_FOUND(HttpStatus.NOT_FOUND, "line-1000", "해당 노선은 존재하지 않습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String serviceErrorCode;
  private final String defaultErrorMessage;

  LineExceptionCode(HttpStatus httpStatus, String serviceErrorCode, String defaultErrorMessage) {
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
