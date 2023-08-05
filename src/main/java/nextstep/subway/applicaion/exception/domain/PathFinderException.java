package nextstep.subway.applicaion.exception.domain;

import org.springframework.http.HttpStatus;

public class PathFinderException extends CustomException{

    public PathFinderException(String message) { super(message); }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
