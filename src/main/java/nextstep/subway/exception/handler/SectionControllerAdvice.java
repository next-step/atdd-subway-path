package nextstep.subway.exception.handler;

import nextstep.subway.exception.AlreadyRegisteredException;
import nextstep.subway.exception.CannotInsertLongerSectionException;
import nextstep.subway.exception.CannotInsertSameDistanceSectionException;
import nextstep.subway.exception.CannotRegisterWithoutRegisteredStation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SectionControllerAdvice {

	@ExceptionHandler({CannotInsertLongerSectionException.class, CannotInsertSameDistanceSectionException.class})
	public ResponseEntity<Void> distanceExceptionHandler() {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(AlreadyRegisteredException.class)
	public ResponseEntity<Void> alreadyRegisteredExceptionHandler() {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(CannotRegisterWithoutRegisteredStation.class)
	public ResponseEntity<Void> withoutRegisteredStationExceptionHandler() {
		return ResponseEntity.badRequest().build();
	}
}
