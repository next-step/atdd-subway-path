package nextstep.subway.domain.line.exception;

import nextstep.subway.global.SubwayException;

public class NoSuchLineException extends SubwayException {
    public NoSuchLineException(final String message) {
        super(message);
    }

    public static NoSuchLineException from(final Long id) {
        return new NoSuchLineException("지하철 노선을 찾을 수 없습니다 : id=" + id);
    }
}
