package nextstep.subway.exception;

import static java.lang.String.format;

public class InvalidSectionDeleteException extends BadRequestException {

	private static final String EXCEPTION_MESSAGE = "Station '%s' is not exist!";

	public InvalidSectionDeleteException(String message) {
		super(message);
	}

	public static InvalidSectionDeleteException ofNotExistStation(String stationName) {
		return new InvalidSectionDeleteException(format(EXCEPTION_MESSAGE, stationName));
	}

}
