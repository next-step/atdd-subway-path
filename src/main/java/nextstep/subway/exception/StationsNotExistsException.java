package nextstep.subway.exception;

import static java.lang.String.format;

public class StationsNotExistsException extends BadRequestException {

    public StationsNotExistsException(String upStationName, String downStationName) {
        super(format("up station '%s' and down station '%s' both are not exist!", upStationName, downStationName));
    }
}
