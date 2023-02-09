package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionDoesNotHaveAlreadyCreateStationException extends SubwayException {
    public static final String MESSAGE = "구간에 노선에 등록된 역이 없습니다.";

    public SectionDoesNotHaveAlreadyCreateStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
