package nextstep.subway.common.exception;

import lombok.Getter;

@Getter
public class ExceptionResponse {

  private String message;

  private ExceptionResponse(String message) {
    this.message = message;
  }

  public static ExceptionResponse getInstance(String message) {
    return new ExceptionResponse(message);
  }
}