package nextstep.subway.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    ;

    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(final String message, final HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
