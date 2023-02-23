package nextstep.subway.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(value = {IllegalArgumentException.class})
	public ResponseEntity IllegalArgumentException() {
		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	}
}
