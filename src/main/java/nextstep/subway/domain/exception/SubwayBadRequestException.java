package nextstep.subway.domain.exception;

public class SubwayBadRequestException extends SubwayException {

	public SubwayBadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
