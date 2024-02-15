package nextstep.subway.ui;

import nextstep.subway.applicaion.exception.ApplicationException;
import nextstep.subway.domain.exception.DomainException;
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

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationException> handleApplicationException(ApplicationException e) {
        return ResponseEntity.status(e.getStatus()).body(e);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomainException(DomainException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
