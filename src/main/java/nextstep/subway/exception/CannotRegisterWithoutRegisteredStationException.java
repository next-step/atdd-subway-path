package nextstep.subway.exception;

public class CannotRegisterWithoutRegisteredStationException extends RuntimeException {
	public CannotRegisterWithoutRegisteredStationException(String message) {
		super(message);
	}
}
