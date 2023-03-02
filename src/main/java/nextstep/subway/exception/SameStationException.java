package nextstep.subway.exception;

public class SameStationException extends RuntimeException {
	private static final ErrorMessage DEFAULT_ERROR_MESSAGE = ErrorMessage.SHOULD_BE_DIFFERENT_SOURCE_AND_TARGET;

	public SameStationException() {
		super(DEFAULT_ERROR_MESSAGE.getMessage());
	}

	public SameStationException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}

}
