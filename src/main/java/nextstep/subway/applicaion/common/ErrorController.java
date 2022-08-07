package nextstep.subway.applicaion.common;

import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> noElementAdviser(NoSuchElementException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, DuplicateRequestException.class})
    public ResponseEntity<ErrorResponse> noElementAdviser(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
}
