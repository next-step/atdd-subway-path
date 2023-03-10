package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler
	public ResponseEntity<String> handleCannotCreateSection(CannotCreateSectionException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<String> handleCannotFindSection(CannotFindSectionException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<String> handleInvalidArgumentException(InvalidArgumentException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(NotExistedPathException.class)
	public ResponseEntity<String> handleCannotNotExistedPathExceptionException(NotExistedPathException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(NotExistedStationException.class)
	public ResponseEntity<String> handleCannotNotExistedStationExceptionException(
		NotExistedStationException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(SameStationException.class)
	public ResponseEntity<String> handleSameStationExceptionException(SameStationException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}
}
