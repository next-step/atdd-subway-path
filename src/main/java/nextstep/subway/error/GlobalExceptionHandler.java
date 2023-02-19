package nextstep.subway.error;

import nextstep.subway.error.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<?> handleBusinessException(final BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse errorResponse = new ErrorResponse(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(errorResponse);
    }
}
