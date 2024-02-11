package nextstep.subway.path.exception;


import nextstep.subway.common.exception.NotFoundException;

public class PathNotFoundException extends NotFoundException {
    public PathNotFoundException() {
        super("path is unreachable");
    }
}
