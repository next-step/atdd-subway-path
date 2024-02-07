package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SectionInsertionNotValidException extends RuntimeException {
	private HttpStatus status = HttpStatus.BAD_REQUEST;

	public SectionInsertionNotValidException() {
		super();
	}

	public SectionInsertionNotValidException(String message) {
		super(message);
	}

	public SectionInsertionNotValidException(HttpStatus status) {
		super(status.getReasonPhrase());
	}

	public SectionInsertionNotValidException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}