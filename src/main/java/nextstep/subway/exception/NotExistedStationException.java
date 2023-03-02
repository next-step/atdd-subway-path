package nextstep.subway.exception;

public class NotExistedStationException extends RuntimeException {
	private static final ErrorMessage DEFAULT_ERROR_MESSAGE = ErrorMessage.SHOULD_BE_PROVIDED_EXISTED_STATION;

	public NotExistedStationException() {
		super(DEFAULT_ERROR_MESSAGE.getMessage());
	}

	public NotExistedStationException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}

}
