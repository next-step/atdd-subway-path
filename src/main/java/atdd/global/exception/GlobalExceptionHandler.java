package atdd.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFoundException(ServiceNotFoundException e) {
        final String message = e.getMessage();
        log.error("{}: {}", message, e.getParams(), e);
        return Map.of("error", message);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(Exception e) {
        final String message = e.getMessage();
        log.error("{}", message, e);
        return Map.of("error", message);
    }

}
