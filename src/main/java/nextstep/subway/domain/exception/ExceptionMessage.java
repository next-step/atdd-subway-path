package nextstep.subway.domain.exception;

public enum ExceptionMessage {
	DUPLICATE_SECTION("같은 구간으로 등록된게 있습니다."),
	DO_NOT_ADD_SECTION("구간을 등록할 수 없습니다.")
	;

	private String message;

	ExceptionMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
