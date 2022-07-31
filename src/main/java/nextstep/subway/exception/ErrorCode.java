package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	// HttpStatus.BAD_REQUEST
	COULD_NOT_DELETE_SECTION(HttpStatus.BAD_REQUEST.value(), "구간을 삭제할 수 없습니다."),
	SAME_SOURCE_AND_TARGET(HttpStatus.BAD_REQUEST.value(), "출발지와 같은 도착지는 검색할수 없습니다."),
	STATION_NOT_INCLUDE_PATH(HttpStatus.BAD_REQUEST.value(), "역이 경로에 포함되어 있지 않습니다.");
	private final int status;
	private final String message;

	ErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return this.status;
	}

	public String getMessage() {
		return this.message;
	}

}
