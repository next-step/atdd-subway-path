package nextstep.subway.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = SubwayException.class)
    public ResponseEntity<ErrorResponse> handleLineException(SubwayException le){
        ErrorResponse errorResponse = new ErrorResponse(le.getErrorCode(), le.getMessage());
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }
}
