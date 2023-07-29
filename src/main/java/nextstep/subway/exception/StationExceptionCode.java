package nextstep.subway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StationExceptionCode implements ExceptionCode {

  STATION_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "station-1000", "해당 역은 이미 존재합니다."),
  ;

  private final HttpStatus httpStatus;
  private final String serviceErrorCode;
  private final String defaultErrorMessage;

  StationExceptionCode(HttpStatus httpStatus, String serviceErrorCode, String defaultErrorMessage) {
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
