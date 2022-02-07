package nextstep.subway.ui;

import nextstep.subway.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
  @ExceptionHandler({
    DataIntegrityViolationException.class,
    IllegalAddSectionException.class,
    IllegalDeletionException.class,
    DuplicateSectionStationException.class
  })
  public ResponseEntity<Void> handleIllegalArgsException(Exception e) {
    return ResponseEntity.badRequest().build();
  }

  @ExceptionHandler(DuplicateCreationException.class)
  public ResponseEntity<Void> handleConflictException(DuplicateCreationException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Void> handNotFoundException(NotFoundException e) {
    return ResponseEntity.notFound().build();
  }
}
