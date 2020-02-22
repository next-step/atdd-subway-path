package atdd.station.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SubwayExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(SubwayExceptionHandler.class);

    @ExceptionHandler(SubwayException.class)
    protected ResponseEntity<ErrorResponse> subwayException(SubwayException e) {
        logger.error("SubwayException", e);

        final ErrorResponse response = ErrorResponse.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .status(e.getStatus()).build();

        return new ResponseEntity<>(response, response.getStatus());
    }

}
