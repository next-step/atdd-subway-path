package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SectionsExceptionHandler {

    @ExceptionHandler({NotFoundStationException.class, NotFoundSectionException.class, NotFoundBothStationException.class})
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(BusinessException e) {
        ErrorResponse response = ErrorResponse.of(HttpStatus.NOT_FOUND, e);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AlreadyBothStationRegisterException.class, SectionDistanceException.class})
    public ResponseEntity<ErrorResponse> sectionsAddExceptionHandler(BusinessException e) {
        ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({CantDeleteLastSectionException.class})
    public ResponseEntity<ErrorResponse> sectionsDeleteExceptionHandler(BusinessException e) {
        ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
