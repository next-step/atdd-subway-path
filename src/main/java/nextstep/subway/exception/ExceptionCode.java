package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {
  HttpStatus getHttpStatusCode();
  String getServiceErrorCode();
  String getDefaultErrorMessage();
}
