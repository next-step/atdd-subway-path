package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException {

    private final SubwayError subwayError;

    public SubwayException(SubwayError subwayError) {
        super();
        this.subwayError = subwayError;
    }

    public HttpStatus getStatus() {
        return subwayError.getStatus();
    }

    public String getMessage() {
        return subwayError.getMessage();
    }

}
