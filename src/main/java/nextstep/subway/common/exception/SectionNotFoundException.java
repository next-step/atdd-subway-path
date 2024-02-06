package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SectionNotFoundException extends RuntimeException {
	private HttpStatus status = HttpStatus.NOT_FOUND;

	public SectionNotFoundException() {
		super();
	}

	public SectionNotFoundException(String message) {
		super(message);
	}

	public SectionNotFoundException(HttpStatus status) {
		super(status.getReasonPhrase());
	}

	public SectionNotFoundException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}