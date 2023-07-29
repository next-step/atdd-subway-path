package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class PathNotConnectedStationException extends ApiException {

	private static final String MESSAGE = "출발역과 도착역이 서로 연결되어 있지 않습니다.";

	public PathNotConnectedStationException() {
		super(HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
