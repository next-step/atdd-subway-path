package nextstep.subway.path.ui;

import nextstep.subway.line.domain.exception.BothStationAlreadyEnrolledException;
import nextstep.subway.line.domain.exception.InvalidDistanceException;
import nextstep.subway.line.domain.exception.NoneOfStationEnrolledException;
import nextstep.subway.path.exception.SameSourceTargetException;
import nextstep.subway.path.exception.SourceTargetNotReachable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PathExceptionControllerHandler {

    @ExceptionHandler({
            SameSourceTargetException.class,
            SourceTargetNotReachable.class
    })
    protected ResponseEntity handleException(){
        return ResponseEntity.badRequest().build();
    }
}
