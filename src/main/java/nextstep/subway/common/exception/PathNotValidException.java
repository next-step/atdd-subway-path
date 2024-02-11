package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PathNotValidException extends RuntimeException {
	private HttpStatus status = HttpStatus.BAD_REQUEST;

	public PathNotValidException() {
		super();
	}

	public PathNotValidException(String message) {
		super(message);
	}

	public PathNotValidException(HttpStatus status) {
		super(status.getReasonPhrase());
	}

	public PathNotValidException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}