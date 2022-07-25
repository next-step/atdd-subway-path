package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;

public class AlreadyBothStationRegisterException extends BusinessException {

    private static final String ALREADY_BOTH_STATION_REGISTER_EXCEPTION = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다";

    public AlreadyBothStationRegisterException() {
        super(ALREADY_BOTH_STATION_REGISTER_EXCEPTION);
    }
}
