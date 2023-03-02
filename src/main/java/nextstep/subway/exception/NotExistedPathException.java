package nextstep.subway.exception;

public class NotExistedPathException extends RuntimeException {
	private static final ErrorMessage DEFAULT_ERROR_MESSAGE = ErrorMessage.SHOULD_EXIST_PATH;

	public NotExistedPathException() {
		super(DEFAULT_ERROR_MESSAGE.getMessage());
	}

	public NotExistedPathException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}

}
