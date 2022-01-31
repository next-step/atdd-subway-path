package nextstep.subway.exception;

import static java.lang.String.format;

public class StationsNotExistsException extends BadRequestException {

    private static final String EXCEPTION_MESSAGE = "up station '%s' and down station '%s' both are not exist!";

    public StationsNotExistsException(String upStationName, String downStationName) {
        super(format(EXCEPTION_MESSAGE, upStationName, downStationName));
    }
}
