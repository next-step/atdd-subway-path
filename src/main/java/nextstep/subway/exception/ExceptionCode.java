package nextstep.subway.exception;

public enum ExceptionCode {
	NOT_FOUND_PATH(100, "경로를 찾을 수 없습니다."),
	NO_ENTITY(200, "존재하지 않는 역입니다."),
	DUPLICATE_SECTION(300,"같은 구간으로 등록된게 있습니다."),
	DO_NOT_ADD_SECTION(310, "구간을 등록할 수 없습니다."),
	NOT_REMOVE_SECTION(320, "구간을 삭제할 수 없습니다.")
	;

	int code;
	String message;

	ExceptionCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
