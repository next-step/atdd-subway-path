package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

  private final HttpStatus httpStatus;

  public CustomException(HttpStatus httpStatus, String message) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}