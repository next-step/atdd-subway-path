package nextstep.subway.domain.exception;

public class SubwayException extends RuntimeException {

	protected final ErrorCode errorCode;

	public SubwayException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public String getCode() {
		return this.errorCode.getCode();
	}
}
