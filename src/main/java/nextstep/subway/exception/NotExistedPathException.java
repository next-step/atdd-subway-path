package nextstep.subway.exception;

public class NotExistedPathException extends RuntimeException {
	public NotExistedPathException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}

}
