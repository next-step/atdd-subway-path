package nextstep.subway.ui.error;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ErrorResponse {

	private HttpStatus status;
	private List<String> errorMessages;

	public ErrorResponse(HttpStatus status, List<String> errorMessages) {
		this.status = status;
		this.errorMessages = errorMessages;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public static ErrorResponse of(HttpStatus status, List<String> errorMessages) {
		return new ErrorResponse(status,errorMessages);
	}
}
