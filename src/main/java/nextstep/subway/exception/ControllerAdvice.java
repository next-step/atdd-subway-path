package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = InSectionDistanceException.class)
    public ResponseEntity<ErrorResponse> inSectionDistanceException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = SectionAllStationsAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> alreadyExistSectionException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = NewlySectionUpStationAndDownStationNotExist.class)
    public ResponseEntity<ErrorResponse> newlySectionUpStationAndDownStationNotExist(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = SectionNotExistException.class)
    public ResponseEntity<ErrorResponse> sectionNotExistException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(e.getMessage()));
    }

}
