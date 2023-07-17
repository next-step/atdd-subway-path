package nextstep.subway.ui;

import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.NoConnectedSectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateSectionException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DuplicateSectionException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidDistanceException.class)
    public ResponseEntity<Void> handleIllegalArgsException(InvalidDistanceException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoConnectedSectionException.class)
    public ResponseEntity<Void> handleIllegalArgsException(NoConnectedSectionException e) {
        return ResponseEntity.badRequest().build();
    }
}
