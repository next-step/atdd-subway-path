package nextstep.subway.exception;

public class AlreadyRegisteredException extends RuntimeException{
	public AlreadyRegisteredException() {
	}

	public AlreadyRegisteredException(String message) {
		super(message);
	}

	public AlreadyRegisteredException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyRegisteredException(Throwable cause) {
		super(cause);
	}
}
