package nextstep.config.fixtures;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.entity.Line;

public class LineFixture {
    public static final LineRequest 신분당선 =
            new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    public static final LineRequest 분당선 =
            new LineRequest("분당선", "bg-green-600", 2L, 4L, 20);
    public static final LineRequest 신림선 =
            new LineRequest("신림선", "bg-blue-600", 6L, 10L, 20);
    public static final LineRequest 수정된_신분당선 =
            new LineRequest("수정된_신분당선", "bg-blue-100", 1L, 2L, 10);
    public static LineRequest 호남선_생성(Long upStationId, Long downStationId) {
        return new LineRequest("호남선", "bg-blue-100", upStationId, downStationId, 10);};
    public static Line 이호선_생성() {
        return new Line("이호선", "그린", 10);
    }
}
