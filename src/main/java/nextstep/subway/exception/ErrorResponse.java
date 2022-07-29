package nextstep.subway.exception;

public class ErrorResponse {
	private int status;
	private String message;

	private ErrorResponse() {
	}

	private ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public static ErrorResponse of(int status, String message) {
		return new ErrorResponse(status, message);
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
