package subway.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exception.dto.ErrorResponse;
import subway.exception.error.ServerErrorCode;

@ControllerAdvice
@Slf4j
public class BindingExceptionHandler {

    // @ModelAttribute 바인딩 파라미터 검증의 경우 BindException 핸들링
    @ExceptionHandler({BindException.class})
    public ResponseEntity<?> errorValid(BindException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder sb = new StringBuilder();
        sb.append("parameter: binding error message");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append(": ");
            sb.append(fieldError.getDefaultMessage());
            sb.append(System.lineSeparator());
        }
        log.error(sb.toString());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(HttpStatus.BAD_REQUEST, ServerErrorCode.BAD_PARAMETER.getMessage()));
    }

    // @RequestBody 바인딩 파라미터 검증의 경우 MethodArgumentNotValidException 핸들링
    // ...

}
