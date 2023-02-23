package nextstep.subway.exception;

public class CannotFindFinalStationException extends RuntimeException {
	public CannotFindFinalStationException(ErrorMessage message) {
		super(message.getMessage());
	}
}
