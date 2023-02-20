package nextstep.subway.exception;

public class InvalidArgumentException extends RuntimeException {
	public InvalidArgumentException(ErrorMessage message) {
		super(message.getMessage());
	}
}
