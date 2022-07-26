package nextstep.subway.common.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ExceptionResponse> notFoundException(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ExceptionResponse.getInstance(e.getMessage()));
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ExceptionResponse> customException(CustomException e) {
    return ResponseEntity.status(e.getHttpStatus())
        .body(ExceptionResponse.getInstance(e.getMessage()));
  }
}