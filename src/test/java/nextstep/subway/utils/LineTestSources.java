package nextstep.subway.utils;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

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

    public static Section section(final Station upStation, final Station downStation) {
        return new Section(upStation, downStation, 10);
    }

    public static Section section(final Station upStation, final Station downStation, final int distance) {
        return new Section(upStation, downStation, distance);
    }

}
