package nextstep.subway.exception;

import static java.lang.String.format;

public class StationsExistException extends BadRequestException {

    public StationsExistException(String upStationName, String downStationName) {
        super(format("up station '%s' and down station '%s' are already exist!", upStationName, downStationName));
    }
}
