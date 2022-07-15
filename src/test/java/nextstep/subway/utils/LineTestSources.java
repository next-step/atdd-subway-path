package nextstep.subway.utils;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;

public class LineTestSources {
    public static final long lineId = 1L;

    public static Line line() {
        return new Line("name", "color");
    }

    public static LineRequest lineRequest() {
        return new LineRequest("name", "color");
    }

    public static LineRequest lineRequest(final long upStationId, final long downStationId) {
        return new LineRequest("name", "color", upStationId, downStationId, 10);
    }

    public static SectionRequest sectionRequest(final long upStationId, final long downStationId) {
        return new SectionRequest(upStationId, downStationId, 10);
    }

}
