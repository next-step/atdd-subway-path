package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionDuplicationStationException extends ApiException {

    public static final String MESSAGE = "중복된 역이 있습니다.";

    public SectionDuplicationStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
