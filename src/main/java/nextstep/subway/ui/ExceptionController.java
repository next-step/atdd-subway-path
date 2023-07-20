package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.ErrorResponse;
import nextstep.subway.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateSectionException.class)
    public ErrorResponse handleIllegalArgsException(DuplicateSectionException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDistanceException.class)
    public ErrorResponse handleIllegalArgsException(InvalidDistanceException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoConnectedSectionException.class)
    public ErrorResponse handleIllegalArgsException(NoConnectedSectionException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.toString());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundStationException.class)
    public ErrorResponse handleIllegalArgsException(NotFoundStationException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.toString());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundLineException.class)
    public ErrorResponse handleIllegalArgsException(NotFoundLineException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LastSectionException.class)
    public ErrorResponse handleIllegalArgsException(LastSectionException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.toString());
    }
}
