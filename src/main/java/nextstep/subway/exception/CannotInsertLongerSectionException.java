package nextstep.subway.exception;

public class CannotInsertLongerSectionException extends RuntimeException {
	public CannotInsertLongerSectionException() {
		super();
	}

	public CannotInsertLongerSectionException(String message) {
		super(message);
	}

	public CannotInsertLongerSectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotInsertLongerSectionException(Throwable cause) {
		super(cause);
	}
}
