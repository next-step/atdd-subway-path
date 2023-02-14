package nextstep.subway.ui;

import nextstep.subway.exception.*;
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

    @ExceptionHandler({
        BothSectionStationsNotExistsInLineException.class,
        CannotDeleteSoleSectionException.class,
        InvalidSectionDistanceException.class,
        SectionStationsAlreadyExistsInLineException.class,
        SectionWithStationNotExistsException.class,
        IdenticalSourceTargetNotAllowedException.class
    })
    public ResponseEntity<String> handleCustomExceptions(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
