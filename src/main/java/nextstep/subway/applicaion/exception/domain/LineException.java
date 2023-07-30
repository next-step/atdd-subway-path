package nextstep.subway.applicaion.exception.domain;

import org.springframework.http.HttpStatus;

public class LineException extends CustomException {

    private static final String MESSAGE = "CHECK_LINE_ID";

    public LineException() {
        super(MESSAGE);
    }

    public LineException(String message) { super(message); }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
