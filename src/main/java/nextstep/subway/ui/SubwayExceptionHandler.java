package nextstep.subway.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.domain.exception.SubwayBadRequestException;

@Slf4j
@RestControllerAdvice
public class SubwayExceptionHandler {
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(SubwayBadRequestException.class)
	public ResponseEntity<Void> handleSubwayBadRequestException(SubwayBadRequestException exception) {
		log.info("Subway Exception message : {} code : {}", exception.getMessage(), exception.getCode());
		return ResponseEntity.badRequest().build();
	}
}
