package nextstep.subway.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = LineException.class)
    public ResponseEntity<ErrorResponse> handleLineException(LineException le){
        ErrorResponse errorResponse = new ErrorResponse(le.getErrorCode(), le.getMessage());
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }
}
