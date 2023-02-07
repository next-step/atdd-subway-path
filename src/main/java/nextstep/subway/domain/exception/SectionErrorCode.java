package nextstep.subway.domain.exception;

public enum SectionErrorCode implements ErrorCode {
	SINGLE_SECTION("지하철 노선에 상행 종점역과 하행 종점역만 존재합니다.");

	private final String message;

	SectionErrorCode(String message) {
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
