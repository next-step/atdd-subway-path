package nextstep.subway.exceptions.errors;

public class SubwayException extends RuntimeException {
  private final SubwayErrorCode errorCode;

  public SubwayException(SubwayErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public SubwayErrorCode getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return errorCode.getMessage();
  }
}
