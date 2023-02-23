package nextstep.subway.exception;

public class CannotFindSectionException extends RuntimeException {
	public CannotFindSectionException(ErrorMessage message) {
		super(message.getMessage());
	}
}
