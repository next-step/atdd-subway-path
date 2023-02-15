package nextstep.subway.ui.error.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

	private HttpStatus status;

	public BusinessException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.status = errorCode.getStatus();
	}

	public BusinessException(String errorMessage) {
		super(errorMessage);
	}

	public HttpStatus getStatus() {
		return status;
	}
}