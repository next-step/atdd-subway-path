package nextstep.subway.exception.paths;

import nextstep.subway.exception.BusinessException;

public class NotConnectedPathException extends BusinessException {

    private static final String NOT_CONNECTED_PATH_EXCEPTION = "도달할 수 없는 역의 최단경로를 찾을 수 없습니다";

    public NotConnectedPathException() {
        super(NOT_CONNECTED_PATH_EXCEPTION);
    }
}
