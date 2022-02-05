package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.ErrorResponse;
import nextstep.subway.applicaion.exception.NotRegisterStation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(NotRegisterStation.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(NotRegisterStation e) {
        return ResponseEntity.badRequest()
                             .body(new ErrorResponse(
                                     e.getStatus(),
                                     e.getReason(),
                                     e.getDate()
                             ));
    }
}
