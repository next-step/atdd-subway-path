package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.ErrorResponse;
import nextstep.subway.applicaion.exception.NotRegisterStationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(NotRegisterStationException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(NotRegisterStationException e) {
        return ResponseEntity.badRequest()
                             .body(new ErrorResponse(
                                     e.getStatus(),
                                     e.getReason(),
                                     e.getDate()
                             ));
    }
}
