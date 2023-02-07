package nextstep.subway.domain.exception;

public enum StationErrorCode implements ErrorCode {

	NOT_FOUND_STATION("요청한 역을 찾을 수 없습니다.");

	private final String message;

	StationErrorCode(String message) {
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
