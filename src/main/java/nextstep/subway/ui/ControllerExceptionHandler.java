package nextstep.subway.ui;

import nextstep.subway.applicaion.LineNotFoundException;
import nextstep.subway.applicaion.StationNotFoundException;
import nextstep.subway.domain.path.DepartureDestinationCannotReachableException;
import nextstep.subway.domain.path.DepartureDestinationCannotSameException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({StationNotFoundException.class, LineNotFoundException.class, DepartureDestinationCannotReachableException.class, DepartureDestinationCannotSameException.class})
    public ResponseEntity<Void> handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
