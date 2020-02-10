package atdd.station;

import atdd.station.domain.Station;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Station> handleIllegalArgumentException() {
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
}
