package nextstep.subway.exception;

public class CannotFindFinalStation extends RuntimeException {
	public CannotFindFinalStation(ErrorMessage message) {
		super(message.getMessage());
	}
}
