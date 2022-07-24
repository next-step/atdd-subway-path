package nextstep.subway.exception.handler;

import nextstep.subway.exception.CannotInsertLongerSectionException;
import nextstep.subway.exception.CannotInsertSameDistanceSectionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SectionControllerAdvice {

	@ExceptionHandler({CannotInsertLongerSectionException.class, CannotInsertSameDistanceSectionException.class})
	public ResponseEntity<Void> distanceExceptionHandler() {
		return ResponseEntity.badRequest().build();
	}
}
