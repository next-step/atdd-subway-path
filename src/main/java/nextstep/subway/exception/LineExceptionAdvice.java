package nextstep.subway.exception;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class LineExceptionAdvice {

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler({NoSuchElementException.class, IllegalStateException.class,
      IllegalArgumentException.class})
  ResponseEntity<ExceptionResponse> handleLineNotFound(Exception exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ExceptionResponse.from(exception));
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler({LineException.class})
  ResponseEntity<ExceptionResponse> handlePathNotFound(LineException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(CustomExceptionResponse.from(exception));
  }
}
