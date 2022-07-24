package nextstep.subway.common.exception;

import nextstep.subway.common.exception.errorcode.ErrorCode;

public class BusinessException extends RuntimeException {
	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
