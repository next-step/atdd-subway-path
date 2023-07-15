package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionRemoveLastStationException extends ApiException {

    public static final String MESSAGE = "제거할 지하철 구간이 지하철 노선의 하행 종점역이 아닙니다.";

    public SectionRemoveLastStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
