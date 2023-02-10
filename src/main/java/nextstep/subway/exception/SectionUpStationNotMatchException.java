package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionUpStationNotMatchException extends SubwayException {
    public static final String MESSAGE = "노선의 하행역과 구간의 상행역이 일치하지 않습니다.";

    public SectionUpStationNotMatchException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
