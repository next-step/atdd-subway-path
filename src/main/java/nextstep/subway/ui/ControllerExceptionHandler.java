package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.ErrorResponse;
import nextstep.subway.applicaion.exception.NotRegisterSectionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(NotRegisterSectionException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(NotRegisterSectionException e) {
        return ResponseEntity.badRequest()
                             .body(new ErrorResponse(
                                     e.getStatus(),
                                     e.getReason(),
                                     e.getDate()
                             ));
    }
}
