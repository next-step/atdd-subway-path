package nextstep.subway.exception;

import static java.lang.String.format;

public class StationsExistException extends BadRequestException {

    private static final String EXCEPTION_MESSAGE = "up station '%s' and down station '%s' are already exist!";

    public StationsExistException(String upStationName, String downStationName) {
        super(format(EXCEPTION_MESSAGE, upStationName, downStationName));
    }
}
