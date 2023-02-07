package nextstep.subway.domain.exception;

public enum LineErrorCode implements ErrorCode {
	INVALID_SECTION_DISTANCE("잘못된 구간 길이 입니다.");

	private final String message;

	LineErrorCode(String message) {
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
