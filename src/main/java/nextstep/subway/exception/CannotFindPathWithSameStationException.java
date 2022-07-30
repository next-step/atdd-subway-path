package nextstep.subway.exception;

public class CannotFindPathWithSameStationException extends RuntimeException {
	public CannotFindPathWithSameStationException(String message) {
		super(message);
	}
}
