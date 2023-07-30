package nextstep.subway.applicaion.dto.comon;

import lombok.Getter;
import nextstep.subway.exception.SubwayException;

@Getter
public class SubwayExceptionResponse {
  private final String serviceErrorCode;
  private final String errorMessage;

  public SubwayExceptionResponse(SubwayException subwayException) {
    this.errorMessage = subwayException.getMessage();
    this.serviceErrorCode = subwayException.getServiceErrorCode();
  }
}
