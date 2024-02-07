package subway.advice;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<SubwayError> illegalArgumentException(IllegalArgumentException ex) {
		String errorCode = getErrorCode(ex);
		String errorMessage = ex.getMessage();
		return ResponseEntity.badRequest().body(new SubwayError(errorCode, errorMessage));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<SubwayError> entityNotFoundException(EntityNotFoundException ex) {
		String errorCode = getErrorCode(ex);
		String errorMessage = ex.getMessage();
		return ResponseEntity.badRequest().body(new SubwayError(errorCode, errorMessage));
	}

	private String getErrorCode(RuntimeException ex) {
		String[] error = ex.getClass().toString().split("\\.");
		return error[error.length - 1];
	}

	public static class SubwayError {
		private final String errorCode;
		private final String errorMessage;

		public SubwayError(String errorCode, String errorMessage) {
			this.errorCode = errorCode;
			this.errorMessage = errorMessage;
		}

		public String getErrorCode() {
			return errorCode;
		}

		public String getErrorMessage() {
			return errorMessage;
		}
	}
}
