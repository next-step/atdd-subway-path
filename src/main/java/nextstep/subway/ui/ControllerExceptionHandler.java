package nextstep.subway.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgsException(DataIntegrityViolationException e) {
        return getResponseEntity(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return getResponseEntity(e.getMessage());
    }

    private ResponseEntity<ExceptionResponse> getResponseEntity(String e) {
        return new ResponseEntity<>(new ExceptionResponse(e), HttpStatus.BAD_REQUEST);
    }

}
