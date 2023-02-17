package nextstep.subway.ui;

import nextstep.subway.domain.exceptions.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorMessage> exception(BusinessException e) {
        ErrorMessage errorMessage = ErrorMessage.of(e.getLocalizedMessage());
        return ResponseEntity.unprocessableEntity().body(errorMessage);
    }
}

class ErrorMessage {
    private String message;

    static ErrorMessage of(String message) {
        return new ErrorMessage(message);
    }

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
