package nextstep.subway.exception;

public class NotExistedStationException extends RuntimeException {
	public NotExistedStationException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}

}
