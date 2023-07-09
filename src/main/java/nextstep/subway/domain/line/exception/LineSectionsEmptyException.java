package nextstep.subway.domain.line.exception;

import nextstep.subway.global.SubwayException;

public class LineSectionsEmptyException extends SubwayException {
    private static final String DEFAULT_MESSAGE = "노선에 등록된 구간이 없습니다";

    public LineSectionsEmptyException(final String message) {
        super(message);
    }

    public LineSectionsEmptyException() {
        this(DEFAULT_MESSAGE);
    }
}
