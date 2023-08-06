package nextstep.subway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PathExceptionCode implements ExceptionCode {

  UNREACHABLE_PATH(HttpStatus.BAD_REQUEST, "path-1000", "시작역에서 도착역까지 지하철로 접근 할 수 없습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String serviceErrorCode;
  private final String defaultErrorMessage;

  PathExceptionCode(HttpStatus httpStatus, String serviceErrorCode, String defaultErrorMessage) {
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
