package nextstep.subway.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SubwayException.class)
    protected ResponseEntity<?> handleSubwayException(final SubwayException e) {
        logger.error("GlobalExceptionHandler.handleSubwayException" , e);
        final ErrorResponse errorResponse = ErrorResponse.of(e.getMessage(), e.getStatus());
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }
}
