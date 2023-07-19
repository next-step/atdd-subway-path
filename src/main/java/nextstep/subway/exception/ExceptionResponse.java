package nextstep.subway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private String message;

    public ExceptionResponse(Exception exception) {
        this.message = exception.getMessage();
    }


    public static ExceptionResponse from(Exception exception) {
        return new ExceptionResponse(exception);
    }
}
