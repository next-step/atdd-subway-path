package nextstep.subway.common.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum BusinessErrorCode implements ErrorCode {
	ENTITY_NOT_FOUND("Entity Not Found", HttpStatus.BAD_REQUEST),
	NULL_STATUS("입력값이 null입니다.", HttpStatus.BAD_REQUEST),
	INVALID_STATUS("처리 불가능한 상태입니다.", HttpStatus.BAD_REQUEST);

	private String message;

	private HttpStatus httpStatus;

	BusinessErrorCode(String message, HttpStatus httpStatus) {
		this.message = message;
		this.httpStatus = httpStatus;
	}

	@Override
	public String getCode() {
		return this.name();
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

}
