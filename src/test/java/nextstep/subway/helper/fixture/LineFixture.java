package nextstep.subway.helper.fixture;

import nextstep.subway.controller.dto.LineCreateRequestBody;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineFixture {
    public static Line 신분당선_엔티티(Station upStation, Station downStation) {
        return Line.create("신분당선", "bg-red-600", upStation, downStation, 10);
    }

    public static Line 이호선_엔티티(Station upStation, Station downStation) {
        return Line.create("2호선", "bg-green-600", upStation, downStation, 7);
    }

    public static LineCreateRequestBody 신분당선_생성_바디(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "신분당선", "bg-red-600", upStationId, downStationId, 10
        );
    }

    public static LineCreateRequestBody 구호선_생성_바디(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "9호선", "bg-yellow-300", upStationId, downStationId, 12
        );
    }

    public static LineCreateRequestBody 삼호선_생성_바디(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "3호선", "bg-yellow-300", upStationId, downStationId, 10
        );
    }

    public static LineCreateRequestBody 칠호선_생성_바디(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "7호선", "bg-green-300", upStationId, downStationId, 9
        );
    }

    public static LineCreateRequestBody 경의중앙선_생성_바디(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "신분당선", "bg-blue-600", upStationId, downStationId, 14
        );
    }

    public static LineCreateRequestBody 이호선_생성_바디(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "이호선", "bg-green-600", upStationId, downStationId, 10
        );
    }
}
