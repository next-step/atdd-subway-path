package nextstep.subway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SecetionExceptionCode implements ExceptionCode {

  SECTION_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "section-1000", "해당 구간은 이미 존재합니다."),
  CANNOT_DELETE_SECTION(HttpStatus.BAD_REQUEST, "section-1001", "구간을 제거 할 수 없습니다."),
  EXCEED_MAXIMUM_DISTANCE(HttpStatus.BAD_REQUEST, "section-1002", "해당 구간에 허용된 거리를 초과하였습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String serviceErrorCode;
  private final String defaultErrorMessage;

  SecetionExceptionCode(HttpStatus httpStatus, String serviceErrorCode, String defaultErrorMessage) {
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
