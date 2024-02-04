package nextstep.subway.controller;

import nextstep.subway.exception.SectionAddFailureException;
import nextstep.subway.exception.SectionDeleteFailureException;
import nextstep.subway.global.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({
        SectionAddFailureException.class, SectionDeleteFailureException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSectionAddFailureException(RuntimeException e) {
        return ErrorResponse.of(e.getMessage());
    }
}
