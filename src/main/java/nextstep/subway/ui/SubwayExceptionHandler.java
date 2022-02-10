package nextstep.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import nextstep.subway.exception.BusinessException;

@ControllerAdvice
public class SubwayExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Void> businessExceptionHandler(final BusinessException e) {
		return ResponseEntity.status(e.getCode()).build();
	}
}
