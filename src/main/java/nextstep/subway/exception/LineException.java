package nextstep.subway.exception;

public class LineException extends RuntimeException {

  private ErrorCode errorCode;

  public LineException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
