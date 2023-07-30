package nextstep.subway.applicaion.exception.domain;

import org.springframework.http.HttpStatus;

public class StationException extends CustomException{

    public StationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
