package nextstep.subway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    private static final String DEFAULT_ERROR_MESSAGE = "시스템 에러가 발생했습니다.";

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity subwayExceptionHandler(final SubwayException exception) {
        return ResponseEntity.status(exception.getStatus()).body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity ExceptionHandler(final Exception exception) {
        LOGGER.error("ExceptionHandler : {}", exception);
        return ResponseEntity.internalServerError().body(DEFAULT_ERROR_MESSAGE);
    }

}
