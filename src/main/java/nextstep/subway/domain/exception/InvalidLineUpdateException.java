package nextstep.subway.domain.exception;

public class InvalidLineUpdateException extends SubwayException {

	public InvalidLineUpdateException(ErrorCode errorCode) {
		super(errorCode);
	}
}
