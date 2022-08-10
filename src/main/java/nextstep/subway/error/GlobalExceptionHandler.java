package nextstep.subway.error;

import nextstep.subway.error.exception.BusinessException;
import nextstep.subway.error.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice(basePackages = {"nextstep.subway"})
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
        return createErrorResponse(e, NOT_FOUND);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ErrorResponse> handleValidationErrorException(RuntimeException e) {
        return createErrorResponse(e, BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(Exception e, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), httpStatus);
    }
}
