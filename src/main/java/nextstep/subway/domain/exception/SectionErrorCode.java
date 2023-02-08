package nextstep.subway.domain.exception;

public enum SectionErrorCode implements ErrorCode {
	SINGLE_SECTION("지하철 노선에 상행 종점역과 하행 종점역만 존재합니다."),
	INVALID_REMOVE_STATION("구간 제거 시 하행종점역만 가능합니다."),
	NOT_INCLUDE_STATION("노선에 포함되지 않은 지하철역 입니다."),
	HAVE_STATIONS("이미 노선에 등록되어있는 역들 입니다."),
	MORE_LONGER_LENGTH("추가되는 구간 길이가 더 깁니다."),
	NOT_FOUND_EXISTING_STATION("요청한 역들 중 기존 노선에 포함되어있는 역이 없습니다.");

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
