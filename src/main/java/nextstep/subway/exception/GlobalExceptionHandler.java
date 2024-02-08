package nextstep.subway.exception;

import nextstep.subway.FailResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InvalidInputException.class})
    protected ResponseEntity<FailResponse> handleInvalidInputException(InvalidInputException ex) {
        return ResponseEntity.badRequest().body(new FailResponse(ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {PathNotFoundException.class})
    protected ResponseEntity<FailResponse> handlePathNotFoundException(PathNotFoundException ex) {
        return ResponseEntity.badRequest().body(new FailResponse(ex.getMessage()));
    }
}
