package nextstep.subway.config;

import nextstep.subway.exception.DuplicatedStationException;
import nextstep.subway.exception.NoLastStationException;
import nextstep.subway.exception.SectionRegistrationException;
import nextstep.subway.exception.SectionRemovalException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> entityNotFoundException(EntityNotFoundException e) {
        final ErrorResponse errorResponse = new ErrorResponse(EntityNotFoundException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorResponse> emptyResultDataAccessException(EmptyResultDataAccessException e) {
        final ErrorResponse errorResponse = new ErrorResponse(EmptyResultDataAccessException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({SectionRegistrationException.class})
    public ResponseEntity<ErrorResponse> sectionRegistrationException(SectionRegistrationException e) {
        final ErrorResponse errorResponse = new ErrorResponse(SectionRegistrationException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({DuplicatedStationException.class})
    public ResponseEntity<ErrorResponse> duplicatedStationException(DuplicatedStationException e) {
        final ErrorResponse errorResponse = new ErrorResponse(DuplicatedStationException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({NoLastStationException.class})
    public ResponseEntity<ErrorResponse> noLastStationException(NoLastStationException e) {
        final ErrorResponse errorResponse = new ErrorResponse(NoLastStationException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({SectionRemovalException.class})
    public ResponseEntity<ErrorResponse> sectionRemovalException(SectionRemovalException e) {
        final ErrorResponse errorResponse = new ErrorResponse(SectionRemovalException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

}