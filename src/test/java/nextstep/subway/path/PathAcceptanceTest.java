package nextstep.subway.path;

import nextstep.subway.line.acceptance.LineSectionAcceptanceTest;
import nextstep.subway.line.acceptance.LineSteps;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("경로탐색 인수테스트")
public class PathAcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 교대역;
    private StationResponse 삼성역;
    private StationResponse 잠실역;
    private StationResponse 고속터미널역;
    private StationResponse 강남구청역;

    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 칠호선;

    @BeforeEach
    void setUp() {
        강남역 = 지하철역_생성_요청("강남역");
        교대역 = 지하철역_생성_요청("교대역");
        삼성역 = 지하철역_생성_요청("삼성역");
        잠실역 = 지하철역_생성_요청("잠실역");
        고속터미널역 = 지하철역_생성_요청("고속터미널역");
        강남구청역 = 지하철역_생성_요청("강남구청역");

        LineRequest request2line = new LineRequest("이호선", "green", 잠실역.getId(), 삼성역.getId(),10);
        LineRequest request3line = new LineRequest("삼호선", "red", 교대역.getId(), 고속터미널역.getId(),10);
        LineRequest request7line = new LineRequest("칠호선", "grey", 고속터미널역.getId(), 강남구청역.getId(),20);

        이호선 = 노선_생성_요청(request2line);
        삼호선 = 노선_생성_요청(request3line);
        칠호선 = 노선_생성_요청(request7line);

        지하철노선에_지하철역_등록_요청(이호선, 삼성역, 강남역, 20);
        지하철노선에_지하철역_등록_요청(이호선, 강남역, 교대역, 5);
        지하철노선에_지하철역_등록_요청(삼호선, 고속터미널역, 강남구청역,50);
    }

    @DisplayName("두개의 역 간의 최단거리를 조회한다.")
    @Test
    void searchShortPath() {
        //when

        //then
    }

    private LineResponse 지하철노선에_지하철역_등록_요청(LineResponse line, StationResponse upStation, StationResponse downStation, int distance) {
        return LineSectionAcceptanceTest.노선에_지하철역_등록_요청(line, upStation.getId(), downStation.getId(), distance).body().as(LineResponse.class);
    }

    private StationResponse 지하철역_생성_요청(String name) {
        return StationAcceptanceTest.지하철역_생성_요청(name).body().as(StationResponse.class);
    }

    private LineResponse 노선_생성_요청(LineRequest lineRequest) {
        return LineSteps.노선_생성_요청(lineRequest).body().as(LineResponse.class);
    }
}
