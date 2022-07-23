package nextstep.subway.common.exception.errorcode;

public enum BusinessErrorCode implements ErrorCode {
	ENTITY_NOT_FOUND("Entity Not Found"),
	INVALID_STATUS("처리 불가능한 상태입니다.");

	private String message;

	BusinessErrorCode(String message) {
		this.message = message;
	}

	@Override
	public String getCode() {
		return this.name();
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
