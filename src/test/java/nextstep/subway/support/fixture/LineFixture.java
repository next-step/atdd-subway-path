package nextstep.subway.support.fixture;

import java.util.Map;
import nextstep.subway.support.step.StationSteps;
import nextstep.subway.web.dto.response.StationResponse;

public class LineFixture {


    public static final String 일호선 = "일호선";
    public static final String 이호선 = "이호선";
    public static final String 파란색 = "파란색";
    public static final String 초록색 = "초록색";

    public static Map<String, Object> 서울역_청량리역_구간_일호선_생성() {
        StationResponse 서울역 = StationSteps.지하철_역_생성_요청(StationFixture.서울역_생성()).as(StationResponse.class);
        StationResponse 청량리역 = StationSteps.지하철_역_생성_요청(StationFixture.청량리역_생성()).as(StationResponse.class);
        return 노선_생성(일호선, 파란색, 서울역.getId(), 청량리역.getId(),
            10L);
    }

    public static Map<String, Object> 강남역_교대역_구간_이호선_생성() {
        StationResponse 강남역 = StationSteps.지하철_역_생성_요청(StationFixture.강남역_생성()).as(StationResponse.class);
        StationResponse 교대역 = StationSteps.지하철_역_생성_요청(StationFixture.교대역_생성()).as(StationResponse.class);
        return 노선_생성(이호선, 초록색, 강남역.getId(), 교대역.getId(), 10L);
    }


    public static Map<String, Object> 노선_생성(
        String name,
        String color,
        Long upStationId,
        Long downStationId,
        Long distance
    ) {
        return Map.of("name", name, "color", color, "upStationId", upStationId, "downStationId", downStationId,
            "distance", distance);
    }

    public static Map<String, Object> 노선_수정(
        String name,
        String color
    ) {
        return Map.of("name", name, "color", color);
    }


}
