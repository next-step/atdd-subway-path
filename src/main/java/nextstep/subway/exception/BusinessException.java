package nextstep.subway.exception;

public class BusinessException extends RuntimeException{

	private final int code;

	public BusinessException(String message, int code) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
