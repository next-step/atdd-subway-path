package nextstep.subway.exception;

public class SameStationException extends RuntimeException {
	public SameStationException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}

}
