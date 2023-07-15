package nextstep.subway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.dto.ErrorResponse;
import nextstep.subway.exception.ApiException;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        return ResponseEntity.status(e.getStatus())
            .body(new ErrorResponse(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."));
    }
}
