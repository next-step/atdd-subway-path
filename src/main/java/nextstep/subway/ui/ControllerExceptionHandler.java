package nextstep.subway.ui;

import nextstep.subway.common.exception.ErrorResponse;
import nextstep.subway.common.exception.SubwayException;
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

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ErrorResponse> handleSubwayException(SubwayException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getMessage()));
    }
}
