package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionNotConnectingStationException extends ApiException {

    public static final String MESSAGE = "추가될 상행역은 노선에 등록 되어있는 하행종점역이 아닙니다.";

    public SectionNotConnectingStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }

}
