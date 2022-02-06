package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static nextstep.subway.exception.Messages.DUPLICATE_SECTION_STATION;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateSectionStationException extends RuntimeException {
    public DuplicateSectionStationException() {
        super(DUPLICATE_SECTION_STATION.message());
    }
}
