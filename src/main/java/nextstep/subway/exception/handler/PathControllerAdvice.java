package nextstep.subway.exception.handler;

import nextstep.subway.exception.NotRegisteredStationException;
import nextstep.subway.exception.SameStationException;
import nextstep.subway.exception.DisconnectedStationsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static nextstep.subway.exception.ErrorCode.*;

@RestControllerAdvice
public class PathControllerAdvice {

	@ExceptionHandler(SameStationException.class)
	public ResponseEntity<Void> cannotFindPathWithSameStationExceptionHandler() {
		return ResponseEntity.status(CANNOT_FIND_PATH_WITH_SAME_STATION.getStatus()).build();
	}

	@ExceptionHandler(DisconnectedStationsException.class)
	public ResponseEntity<Void> disconnectedStationsExceptionHandler() {
		return ResponseEntity.status(CANNOT_FIND_PATH_WITH_DISCONNECTED_STATIONS.getStatus()).build();
	}

	@ExceptionHandler(NotRegisteredStationException.class)
	public ResponseEntity<Void> notRegisteredStationExceptionHandler() {
		return ResponseEntity.status(CANNOT_FIND_PATH_WITH_NOT_REGISTERED_STATION.getStatus()).build();
	}
}
