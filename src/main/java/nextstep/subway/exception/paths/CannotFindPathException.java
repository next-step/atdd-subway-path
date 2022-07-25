package nextstep.subway.exception.paths;

import nextstep.subway.exception.BusinessException;

public class CannotFindPathException extends BusinessException {

    private static final String CANT_FOUND_SHORT_PATH_EXCEPTION = "출발역, 도착역 중 하나라도 없으면 최단경로를 찾을 수 없습니다";

    public CannotFindPathException() {
        super(CANT_FOUND_SHORT_PATH_EXCEPTION);
    }
}
