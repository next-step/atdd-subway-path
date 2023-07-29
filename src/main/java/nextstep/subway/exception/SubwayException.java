package nextstep.subway.exception;


import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException {

  private final ExceptionCode exceptionCode;

  public SubwayException(ExceptionCode exceptionCode) {
    super(exceptionCode.getDefaultErrorMessage());
    this.exceptionCode = exceptionCode;
  }

  public SubwayException(ExceptionCode exceptionCode, String message) {
    super(message);
    this.exceptionCode = exceptionCode;
  }

  public ExceptionCode getExceptionCode() {
    return exceptionCode;
  }

  public HttpStatus getHttpErrorStatus() {
    return exceptionCode.getHttpStatusCode();
  }

  public String getServiceErrorCode() {
    return exceptionCode.getServiceErrorCode();
  }
}
