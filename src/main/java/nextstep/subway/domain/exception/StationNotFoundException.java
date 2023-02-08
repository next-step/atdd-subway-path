package nextstep.subway.domain.exception;

public class StationNotFoundException extends SubwayException {

	public StationNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
