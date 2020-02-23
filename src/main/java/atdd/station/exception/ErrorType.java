package atdd.station.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
    INTERNAL_SERVER_ERROR(-100, HttpStatus.INTERNAL_SERVER_ERROR, "internal server error"),
    OBJECT_MAPPER_ERROR(-101, HttpStatus.INTERNAL_SERVER_ERROR, "object mapper error"),

    NOT_FOUND_LINE(-200, HttpStatus.NOT_FOUND, "not found line"),

    NOT_FOUND_EDGE(-250, HttpStatus.NOT_FOUND, "not found edge"),
    INVALID_EDGE(-251, HttpStatus.INTERNAL_SERVER_ERROR, "invalid edge");

    private int code;
    private HttpStatus status;
    private String message;

    ErrorType(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
