package nextstep.subway.exception.handler;

import nextstep.subway.exception.*;
import nextstep.subway.ui.LineController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static nextstep.subway.exception.ErrorCode.*;

@RestControllerAdvice(assignableTypes = LineController.class)
public class SectionControllerAdvice {

	@ExceptionHandler(CannotInsertLongerSectionException.class)
	public ResponseEntity<Void> cannotInsertLongerSectionExceptionHandler() {
		return ResponseEntity.status(CANNOT_INSERT_LONGER_SECTION.getStatus()).build();
	}

	@ExceptionHandler(CannotInsertSameDistanceSectionException.class)
	public ResponseEntity<Void> cannotInsertSameDistanceSectionExceptionHandler() {
		return ResponseEntity.status(CANNOT_INSERT_SAME_DISTANCE_SECTION.getStatus()).build();
	}

	@ExceptionHandler(AlreadyRegisteredException.class)
	public ResponseEntity<Void> alreadyRegisteredExceptionHandler() {
		return ResponseEntity.status(CANNOT_REGISTER_ALREADY_REGISTERED_SECTION.getStatus()).build();
	}

	@ExceptionHandler(CannotRegisterWithoutRegisteredStation.class)
	public ResponseEntity<Void> withoutRegisteredStationExceptionHandler() {
		return ResponseEntity.status(CANNOT_REGISTER_WITHOUT_REGISTERED_STATIONS.getStatus()).build();
	}

	@ExceptionHandler(CannotRemoveLastSectionException.class)
	public ResponseEntity<Void> cannotRemoveLastSectionExceptionHandler() {
		return ResponseEntity.status(CANNOT_REMOVE_LAST_SECTION.getStatus()).build();
	}

	@ExceptionHandler(CannotRemoveSectionException.class)
	public ResponseEntity<Void> cannotRemoveSectionException() {
		return ResponseEntity.status(CANNOT_REMOVE_SECTION.getStatus()).build();
	}
}
