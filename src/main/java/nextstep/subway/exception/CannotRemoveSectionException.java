package nextstep.subway.exception;

public class CannotRemoveSectionException extends RuntimeException {
	public CannotRemoveSectionException(ErrorMessage message) {
		super(message.getMessage());
	}
}
