package nextstep.subway.helper.fixture;

import nextstep.subway.controller.dto.LineCreateRequestBody;

public class LineFixture {
    public static LineCreateRequestBody 신분당선(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "신분당선", "bg-red-600", upStationId, downStationId, 10
        );
    }

    public static LineCreateRequestBody 이호선(Long upStationId, Long downStationId) {
        return new LineCreateRequestBody(
                "이호선", "bg-green-600", upStationId, downStationId, 10
        );
    }
}
