package nextstep.subway.helper.fixture;

import nextstep.subway.controller.dto.LineCreateRequestBody;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineFixture {
    public static LineCreateRequestBody 신분당선_생성_바디(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "신분당선", "bg-red-600", upStationId, downStationId, 10
        );
    }

    public static Line 신분당선_엔티티(Station upStation, Station downStation) {
        return Line.create("신분당선", "bg-red-600", upStation, downStation, 10);
    }

    public static LineCreateRequestBody 이호선_생성_바디(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "이호선", "bg-green-600", upStationId, downStationId, 10
        );
    }
}
