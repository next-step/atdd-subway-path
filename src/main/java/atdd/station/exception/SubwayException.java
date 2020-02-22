package atdd.station.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SubwayException extends Exception {
    private int code;
    private HttpStatus status;
    private String message;

    public SubwayException(ErrorType type) {
        this.code = type.getCode();
        this.status = type.getStatus();
        this.message = type.getMessage();
    }
}
