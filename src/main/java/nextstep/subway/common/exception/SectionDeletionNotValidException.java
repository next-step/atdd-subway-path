package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SectionDeletionNotValidException extends RuntimeException {
	private HttpStatus status = HttpStatus.BAD_REQUEST;

	public SectionDeletionNotValidException() {
		super();
	}

	public SectionDeletionNotValidException(String message) {
		super(message);
	}

	public SectionDeletionNotValidException(HttpStatus status) {
		super(status.getReasonPhrase());
	}

	public SectionDeletionNotValidException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}