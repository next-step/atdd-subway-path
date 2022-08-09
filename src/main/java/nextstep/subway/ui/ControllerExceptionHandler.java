package nextstep.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import nextstep.subway.applicaion.dto.ErrorResponse;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(SubwayException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}
