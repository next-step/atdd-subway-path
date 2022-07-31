package nextstep.subway.exception;

public class BusinessException extends RuntimeException {
	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public int getStatus() {
		return errorCode.getStatus();
	}

	public String getMessage() {
		return errorCode.getMessage();
	}

}
