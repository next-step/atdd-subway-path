package nextstep.subway.domain.exception;

public enum PathErrorCode implements ErrorCode {

	EQUAL_SEARCH_STATION("출발역과 도착역은 같지 않아야합니다."),
	NOT_CONNECTION("연결되어있지 않은 역입니다."),
	NOT_FOUND_STATION("해당역은 존재하지 않습니다.");

	private final String message;

	PathErrorCode(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public String getCode() {
		return this.name();
	}
}
