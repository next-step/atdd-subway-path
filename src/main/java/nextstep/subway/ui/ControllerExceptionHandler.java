package nextstep.subway.ui;

import javassist.NotFoundException;
import nextstep.subway.exception.IllegalPathArgumentException;
import nextstep.subway.exception.IllegalSectionArgumentException;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({DataIntegrityViolationException.class, IllegalSectionArgumentException.class, IllegalPathArgumentException.class})
    public ResponseEntity<Void> handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<Void> stationNotFoundException() {
        return ResponseEntity.badRequest().build();
    }
}
