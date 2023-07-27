package nextstep.subway.ui.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("[ControllerExceptionHandler] An MethodArgumentNotValidException occurred");
        e.printStackTrace();

        BindingResult result = e.getBindingResult();

        return ResponseEntity
                .badRequest()
                .body(result.getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handlerIllegalArgumentException(IllegalArgumentException e) {
        log.info("[ControllerExceptionHandler] An MethodArgumentNotValidException occurred");
        e.printStackTrace();

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}