package nextstep.subway.exception;

public class CannotRemoveLastSectionException extends RuntimeException{
	public CannotRemoveLastSectionException() {
	}

	public CannotRemoveLastSectionException(String message) {
		super(message);
	}

	public CannotRemoveLastSectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotRemoveLastSectionException(Throwable cause) {
		super(cause);
	}
}
