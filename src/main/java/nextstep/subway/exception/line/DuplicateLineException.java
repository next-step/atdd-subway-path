package nextstep.subway.exception.line;

import nextstep.subway.exception.ServiceException;

public class DuplicateLineException extends ServiceException {

    private static final String MESSAGE = "노선 이름이 중복 됩니다. - %s";

    public DuplicateLineException(String lineName) {
        super(String.format(MESSAGE, lineName));
    }

}
