package nextstep.subway.exception;

public class CannotRegisterWithoutRegisteredStation extends RuntimeException {
	public CannotRegisterWithoutRegisteredStation() {
		super();
	}

	public CannotRegisterWithoutRegisteredStation(String message) {
		super(message);
	}

	public CannotRegisterWithoutRegisteredStation(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotRegisterWithoutRegisteredStation(Throwable cause) {
		super(cause);
	}
}
