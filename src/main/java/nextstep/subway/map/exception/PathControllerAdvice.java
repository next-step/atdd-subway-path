package nextstep.subway.map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PathControllerAdvice {
    @ExceptionHandler(SameSourceAndTagetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSameSourceAndTagetException() {
        return "출발역과 도착역이 같습니다.";
    }

    @ExceptionHandler(NotConnectedSourceAndTargetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotConnectedSourceAndTargetException() {
        return "출발역과 도착역이 연결이 되어 있지 않습니다";
    }

    @ExceptionHandler(NonExistSourceOrTargetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRemoveNonExistStationException() {
        return "존재하지 않은 출발역이나 도착역을 조회 하였습니다.";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRuntimeException() {
        return "RuntimeException이 발생하였습니다.";
    }
}
