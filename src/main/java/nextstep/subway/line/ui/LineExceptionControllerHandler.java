package nextstep.subway.line.ui;

import nextstep.subway.line.domain.exception.BothStationAlreadyEnrolledException;
import nextstep.subway.line.domain.exception.InvalidDistanceException;
import nextstep.subway.line.domain.exception.NoneOfStationEnrolledException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LineExceptionControllerHandler {

    @ExceptionHandler({
            BothStationAlreadyEnrolledException.class,
            InvalidDistanceException.class,
            NoneOfStationEnrolledException.class
    })
    protected ResponseEntity handleException(){
        return ResponseEntity.badRequest().build();
    }
}
