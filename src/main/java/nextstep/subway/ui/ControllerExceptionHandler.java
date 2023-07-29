package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.comon.SubwayExceptionResponse;
import nextstep.subway.exception.SubwayException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SubwayException.class)
    @ResponseBody
    public ResponseEntity<SubwayExceptionResponse> handleSubwayException(SubwayException e) {
        return ResponseEntity.status(e.getHttpErrorStatus())
            .body(new SubwayExceptionResponse(e));
    }
}
