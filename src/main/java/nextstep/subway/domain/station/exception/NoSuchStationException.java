package nextstep.subway.domain.station.exception;

import nextstep.subway.global.SubwayException;

public class NoSuchStationException extends SubwayException {

    public NoSuchStationException(final String message) {
        super(message);
    }

    public static NoSuchStationException from(final Long id) {
        return new NoSuchStationException("지하철 역을 찾을 수 없습니다 : id=" + id);
    }
}
