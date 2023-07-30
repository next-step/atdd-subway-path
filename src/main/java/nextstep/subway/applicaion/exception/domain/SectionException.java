package nextstep.subway.applicaion.exception.domain;

import org.springframework.http.HttpStatus;

public class SectionException extends CustomException {

    public SectionException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
