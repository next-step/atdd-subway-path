package nextstep.subway.ui.handler;

import nextstep.subway.exception.line.DuplicateLineException;
import nextstep.subway.exception.line.LineNotFoundException;
import nextstep.subway.exception.section.AlreadyRegisteredStationInLineException;
import nextstep.subway.exception.section.DeleteLastDownStationException;
import nextstep.subway.exception.section.DownStationNotMatchException;
import nextstep.subway.exception.section.MinimumSectionException;
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
            AlreadyRegisteredStationInLineException.class,
            DownStationNotMatchException.class,
            DuplicateLineException.class,
            MinimumSectionException.class,
            DeleteLastDownStationException.class})
    public ResponseEntity<ErrorResponse> duplicateStationHandler(RuntimeException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(exception.getMessage()));
    }

}
