package nextstep.subway.exceptions.errors;

import org.springframework.http.HttpStatus;

public class SubwayErrorResponse {
  private HttpStatus status;
  private String statusMessage;

  public SubwayErrorResponse() {
  }

  public SubwayErrorResponse(HttpStatus status, String message) {
    this.status = status;
    this.statusMessage = message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getStatusMessage() {
    return statusMessage;
  }
}
