package nextstep.subway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomExceptionResponse {
  private String message;
  private int errorCode;
  public CustomExceptionResponse(LineException exception) {

    this.message = exception.getErrorCode().getErrorMessage();
    this.errorCode = exception.getErrorCode().getErrorCode();
  }


  public static ExceptionResponse from(Exception exception) {
    return new ExceptionResponse(exception);
  }
}
