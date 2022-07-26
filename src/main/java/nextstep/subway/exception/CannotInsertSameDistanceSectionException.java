package nextstep.subway.exception;
public class CannotInsertSameDistanceSectionException extends RuntimeException{
	public CannotInsertSameDistanceSectionException() {
		super();
	}

	public CannotInsertSameDistanceSectionException(String message) {
		super(message);
	}

	public CannotInsertSameDistanceSectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotInsertSameDistanceSectionException(Throwable cause) {
		super(cause);
	}
}
