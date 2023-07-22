package nextstep.subway.applicaion.exception.domain;

import nextstep.subway.applicaion.exception.CustomException;
import org.springframework.http.HttpStatus;

public class LineException extends CustomException {

    private static final String MESSAGE = "CHECK_LINE_ID";

    public LineException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
