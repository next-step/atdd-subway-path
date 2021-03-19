package nextstep.subway;

import nextstep.subway.common.exception.InvalidSectionException;
import nextstep.subway.common.exception.InvalidStationPathException;
import nextstep.subway.common.exception.NoResourceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoResourceException.class)
  public ResponseEntity handleNoResourceException(NoResourceException e) {
    return ResponseEntity.notFound().build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
    return ResponseEntity.badRequest().build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidSectionException.class)
  public ResponseEntity handleInvalidSectionException(InvalidSectionException e) {
    return ResponseEntity.badRequest().build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidStationPathException.class)
  public ResponseEntity handleInvalidSectionException(InvalidStationPathException e) {
    return ResponseEntity.badRequest().build();
  }
}
