package nextstep.subway.ui.handler;

import nextstep.subway.exception.line.DuplicateLineException;
import nextstep.subway.exception.line.LineNotFoundException;
import nextstep.subway.exception.section.*;
import nextstep.subway.exception.station.DuplicateStationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdviser {

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<Void> lineNotFoundHandler(LineNotFoundException exception) {
        return ResponseEntity.notFound()
                .build();
    }

    @ExceptionHandler({
            DuplicateStationException.class,
            DuplicateLineException.class,
            MinimumSectionException.class,
            DeleteLastDownStationException.class,
            MinimumDistanceException.class,
            InvalidDistanceException.class,
            AlreadyRegisteredStationException.class,
            NotFoundConnectStationException.class})
    public ResponseEntity<ErrorResponse> duplicateStationHandler(RuntimeException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(exception.getMessage()));
    }

}
