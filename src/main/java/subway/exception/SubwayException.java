package subway.exception;

import org.springframework.http.HttpStatus;
import subway.exception.error.SubwayErrorCode;

public class SubwayException extends RuntimeException {

    private final HttpStatus statusCode;

    public SubwayException(SubwayErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

}
