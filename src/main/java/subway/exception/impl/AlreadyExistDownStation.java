package subway.exception.impl;

import subway.exception.SubwayException;
import subway.exception.error.SubwayErrorCode;

public class AlreadyExistDownStation extends SubwayException {

    public AlreadyExistDownStation() {
        super(SubwayErrorCode.ALREADY_EXIST_DOWN_STATION);
    }

}
