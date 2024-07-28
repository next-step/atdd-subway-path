package nextstep.subway.exceptions;

import nextstep.subway.exceptions.errors.SubwayErrorCode;
import nextstep.subway.exceptions.errors.SubwayErrorResponse;
import nextstep.subway.exceptions.errors.SubwayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;
import java.util.function.Function;

@ControllerAdvice
public class SubwayExceptionHandler {

  @ExceptionHandler(SubwayException.class)
  public ResponseEntity<SubwayErrorResponse> handleSubwayException(SubwayException error) {
    return Optional.of(error)
      .map(SubwayException::getErrorCode)
      .map(createErrorResponse())
      .map(createResponseEntity())
      .orElseThrow(() -> new RuntimeException("Unexpected error occurred"));
  }

  private Function<SubwayErrorCode, SubwayErrorResponse> createErrorResponse() {
    return status -> new SubwayErrorResponse(status.getStatus(), status.getMessage());
  }

  private Function<SubwayErrorResponse, ResponseEntity<SubwayErrorResponse>> createResponseEntity() {
    return response -> new ResponseEntity<>(response, response.getStatus());
  }
}
