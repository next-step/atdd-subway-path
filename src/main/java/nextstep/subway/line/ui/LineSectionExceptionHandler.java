package nextstep.subway.line.ui;

import nextstep.subway.error.NameExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LineSectionExceptionHandler {

    @ExceptionHandler(NameExistsException.class)
    public ResponseEntity handleNameArgsException(NameExistsException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({RuntimeException.class, IllegalArgumentException.class})
    public ResponseEntity handleIllegalArgsException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
