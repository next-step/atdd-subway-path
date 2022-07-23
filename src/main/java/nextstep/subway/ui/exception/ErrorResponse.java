package nextstep.subway.ui.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final Integer statusCode;
    private final String message;

    public static ErrorResponse of(Integer statusCode, String message) {
        return new ErrorResponse(statusCode, message);
    }
}
