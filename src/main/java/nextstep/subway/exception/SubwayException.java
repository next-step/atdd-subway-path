package nextstep.subway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SubwayException extends RuntimeException {
    private final HttpStatus status;
    public SubwayException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
