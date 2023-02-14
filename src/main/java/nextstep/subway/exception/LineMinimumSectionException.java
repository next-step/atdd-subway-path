package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class LineMinimumSectionException extends SubwayException {
    public static final String MESSAGE = "구간에 이미 노선에 등록된 지하철역이 있습니다.";

    public LineMinimumSectionException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
