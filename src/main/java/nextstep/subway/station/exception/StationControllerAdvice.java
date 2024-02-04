package nextstep.subway.station.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice()
public class StationControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(StationControllerAdvice.class);

    @ExceptionHandler(StationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String stationException(StationException e) {
        log.debug("StationException 발생 ::: {}", e.getMessage());
        return e.getMessage();
    }
}
