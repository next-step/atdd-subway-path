package nextstep.subway.support.fixture;

import java.util.Map;
import nextstep.subway.domians.domain.Line;
import nextstep.subway.domians.domain.Station;
import nextstep.subway.support.step.StationSteps;
import nextstep.subway.web.dto.response.StationResponse;
import org.springframework.test.util.ReflectionTestUtils;
import static nextstep.subway.support.fixture.StationFixture.강남역_이름;
import static nextstep.subway.support.fixture.StationFixture.교대역_이름;

public class LineFixture {


    public static final String 일호선_이름 = "일호선";
    public static final String 이호선_이름 = "이호선";
    public static final String 일호선_색 = "파란색";
    public static final String 이호선_색 = "초록색";

    public static Map<String, Object> 서울역_청량리역_구간_일호선_생성_요청() {
        StationResponse 서울역 = StationSteps.지하철_역_생성_요청(StationFixture.서울역_생성_요청()).as(StationResponse.class);
        StationResponse 청량리역 = StationSteps.지하철_역_생성_요청(StationFixture.청량리역_생성_요청()).as(StationResponse.class);
        return 노선_생성_요청(일호선_이름, 일호선_색, 서울역.getId(), 청량리역.getId(),
            10L);
    }

    public static Map<String, Object> 강남역_교대역_구간_이호선_생성_요청() {
        StationResponse 강남역 = StationSteps.지하철_역_생성_요청(StationFixture.강남역_생성_요청()).as(StationResponse.class);
        StationResponse 교대역 = StationSteps.지하철_역_생성_요청(StationFixture.교대역_생성_요청()).as(StationResponse.class);
        return 노선_생성_요청(이호선_이름, 이호선_색, 강남역.getId(), 교대역.getId(), 10L);
    }

    public static Map<String, Object> 강남역_봉천역_구간_이호선_생성_요청(long distance) {
        StationResponse 강남역 = StationSteps.지하철_역_생성_요청(StationFixture.강남역_생성_요청()).as(StationResponse.class);
        StationResponse 봉천역 = StationSteps.지하철_역_생성_요청(StationFixture.봉천역_생성_요청()).as(StationResponse.class);
        return 노선_생성_요청(이호선_이름, 이호선_색, 강남역.getId(), 봉천역.getId(), distance);
    }



    public static Map<String, Object> 노선_생성_요청(
        String name,
        String color,
        Long upStationId,
        Long downStationId,
        Long distance
    ) {
        return Map.of("name", name, "color", color, "upStationId", upStationId, "downStationId", downStationId,
            "distance", distance);
    }

    public static Map<String, Object> 노선_수정_요청(
        String name,
        String color
    ) {
        return Map.of("name", name, "color", color);
    }


    public static Line giveOne(long id, String name, String color) {
        Line line = new Line(name, color);
        ReflectionTestUtils.setField(line, "id", id);
        return line;
    }
}
