package nextstep.subway.exceptions.errors;

import org.springframework.http.HttpStatus;

public enum SubwayErrorCode {
  BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "올바르지 않은 파라미터입니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 정보입니다.");

  private final HttpStatus status;
  private final int statusCode;
  private final String message;

  SubwayErrorCode(HttpStatus status, int statusCode, String message) {
    this.status = status;
    this.statusCode = statusCode;
    this.message = message;
  }

  public HttpStatus getStatus(){
    return status;
  }

  public String getMessage() {
    return message;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
