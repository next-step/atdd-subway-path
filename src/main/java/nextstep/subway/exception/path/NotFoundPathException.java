package nextstep.subway.exception.path;

import nextstep.subway.exception.ServiceException;

public class NotFoundPathException extends ServiceException {

    private static final String MESSAGE = "경로를 찾을 수 없습니다. - %s -> %s";

    public NotFoundPathException(String source, String target) {
        super(String.format(MESSAGE, source, target));
    }

}
