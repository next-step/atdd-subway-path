package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.ErrorResponse;
import nextstep.subway.exception.AllStationsOfSectionExistException;
import nextstep.subway.exception.NonStationOfSectionExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ExceptionHandler(AllStationsOfSectionExistException.class)
    public ResponseEntity<ErrorResponse> handleAllStationsOfSectionExistException(AllStationsOfSectionExistException e){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getErrorMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getHttpStatusCode()));
    }

    @ExceptionHandler(NonStationOfSectionExistsException.class)
    public ResponseEntity<ErrorResponse> handleNonStationOfSectionExistsException(NonStationOfSectionExistsException e){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getErrorMessage());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(errorResponse.getHttpStatusCode()));
    }
}
