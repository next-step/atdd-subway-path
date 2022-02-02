package nextstep.subway.exception;

import static java.lang.String.format;

public class StationsNotConnectedException extends BadRequestException {
	public StationsNotConnectedException(String sourceStation, String targetStation) {
		super(format("The source station '%s' and the target station '%s' are not connected", sourceStation, targetStation));
	}
}
