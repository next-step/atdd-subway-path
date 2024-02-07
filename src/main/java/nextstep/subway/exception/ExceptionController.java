package nextstep.subway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionController {
    private final Logger logger = LoggerFactory.getLogger("subway.exception.ExceptionController");

    @ExceptionHandler(LineSectionException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> exception(LineSectionException e, HttpServletRequest req) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage());
        logger.warn("LineSectionException occurred on {} {}. Message={}",
                req.getMethod(), req.getRequestURI(), e.getMessage()
        );
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }
}
