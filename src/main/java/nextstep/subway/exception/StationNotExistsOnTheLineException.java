package nextstep.subway.exception;

import static java.lang.String.format;

public class StationNotExistsOnTheLineException extends BadRequestException {

	public StationNotExistsOnTheLineException(String stationName) {
		super(format("Station '%s' does not exist on lines", stationName));
	}
}
