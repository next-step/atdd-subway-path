package nextstep.subway.exception;

public class CannotRemoveSectionException extends RuntimeException {
	public CannotRemoveSectionException() {
	}

	public CannotRemoveSectionException(String message) {
		super(message);
	}

	public CannotRemoveSectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotRemoveSectionException(Throwable cause) {
		super(cause);
	}
}
