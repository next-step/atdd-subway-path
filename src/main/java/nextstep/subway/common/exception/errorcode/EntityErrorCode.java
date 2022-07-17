package nextstep.subway.common.exception.errorcode;

public enum EntityErrorCode implements ErrorCode {
	ENTITY_NOT_FOUND("Entity Not Found");

	private String message;

	EntityErrorCode(String message) {
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
