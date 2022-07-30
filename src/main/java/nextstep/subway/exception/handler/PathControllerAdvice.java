package nextstep.subway.exception.handler;

import nextstep.subway.exception.CannotFindPathWithSameStationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static nextstep.subway.exception.ErrorCode.CANNOT_FIND_PATH_WITH_SAME_STATION;

@RestControllerAdvice
public class PathControllerAdvice {

	@ExceptionHandler(CannotFindPathWithSameStationException.class)
	public ResponseEntity<Void> cannotFindPathWithSameStationExceptionHandler() {
		return ResponseEntity.status(CANNOT_FIND_PATH_WITH_SAME_STATION.getStatus()).build();
	}
}
