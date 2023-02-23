package nextstep.subway.exception;

public class CannotCreateSectionException extends RuntimeException {
	public CannotCreateSectionException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}
}
