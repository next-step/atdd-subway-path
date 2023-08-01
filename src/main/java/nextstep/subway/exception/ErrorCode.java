package nextstep.subway.exception;

public enum ErrorCode {
  PATH_NOT_FOUND(12, "경로를 찾을 수 없습니다."),
  STATION_NOT_FOUND(13, "역을 찾을 수 없습니다.");

  private int errorCode;
  private String errorMessage;

  ErrorCode(int errorCode, String message) {
    this.errorCode = errorCode;
    this.errorMessage = message;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
