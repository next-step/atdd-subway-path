package nextstep.subway.applicaion.exception.domain;

import nextstep.subway.applicaion.exception.CustomException;
import org.springframework.http.HttpStatus;

public class SectionException extends CustomException {

    private static final String MESSAGE = "CHECK_SECTION_ID";

    public SectionException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
