package nextstep.subway.ui;

import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleBusinessException(ConstraintViolationException e) {
        log.info(e.getMessage());

        return ResponseEntity
            .badRequest()
            .body(e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException e) {
        log.info(e.getMessage());

        return ResponseEntity
            .badRequest()
            .body(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage());
        e.printStackTrace();

        return ResponseEntity
            .internalServerError()
            .body("undefined error occurred");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();

        return ResponseEntity
            .internalServerError()
            .body("undefined error occurred");
    }
}
