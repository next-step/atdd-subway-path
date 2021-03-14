package nextstep.subway.common;

import nextstep.subway.exception.NoOtherStationException;
import nextstep.subway.exception.NotEqualsNameException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.SubwayNameDuplicateException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({
            NoOtherStationException.class,
            NotEqualsNameException.class,
            NotFoundException.class,
            SubwayNameDuplicateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String exceptionCheck() {
        return "예외가 발생하였습니다.";
    }
}
