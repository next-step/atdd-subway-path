package nextstep.subway.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import nextstep.subway.global.SubwayException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ExceptionResponse> handleException(final SubwayException e) {
        return ResponseEntity
                .badRequest()
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(final DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }
}
