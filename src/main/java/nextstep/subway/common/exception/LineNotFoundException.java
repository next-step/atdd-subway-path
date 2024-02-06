package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LineNotFoundException extends RuntimeException {
	private HttpStatus status = HttpStatus.NOT_FOUND;

	public LineNotFoundException() {
		super();
	}

	public LineNotFoundException(String message) {
		super(message);
	}

	public LineNotFoundException(HttpStatus status) {
		super(status.getReasonPhrase());
	}

	public LineNotFoundException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}