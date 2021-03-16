package nextstep.subway.common;

import nextstep.subway.line.exception.DownStationExistedException;
import nextstep.subway.line.exception.HasNoneOrOneSectionException;
import nextstep.subway.line.exception.InvalidSectionDistanceException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.NotValidUpStationException;
import nextstep.subway.line.exception.SectionDuplicatedException;
import nextstep.subway.line.exception.SectionNotConnectedException;
import nextstep.subway.line.exception.StationNotRegisteredException;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handle(HttpServletResponse response, DataIntegrityViolationException e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(StationNotFoundException.class)
    public void handle(HttpServletResponse response, StationNotFoundException e) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(LineNotFoundException.class)
    public void handle(HttpServletResponse response, LineNotFoundException e) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(NotValidUpStationException.class)
    public void handle(HttpServletResponse response, NotValidUpStationException e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(DownStationExistedException.class)
    public void handle(HttpServletResponse response, DownStationExistedException e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(HasNoneOrOneSectionException.class)
    public void handle(HttpServletResponse response, HasNoneOrOneSectionException e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(NotLastStationException.class)
    public void handle(HttpServletResponse response, NotLastStationException e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(InvalidSectionDistanceException.class)
    public void handle(HttpServletResponse response, InvalidSectionDistanceException e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(SectionDuplicatedException.class)
    public void handle(HttpServletResponse response, SectionDuplicatedException e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(SectionNotConnectedException.class)
    public void handle(HttpServletResponse response, SectionNotConnectedException e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(StationNotRegisteredException.class)
    public void handle(HttpServletResponse response, StationNotRegisteredException e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
