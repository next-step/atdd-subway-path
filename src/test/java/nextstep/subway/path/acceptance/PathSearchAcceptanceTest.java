package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.path.acceptance.step.PathSearchAcceptanceStep.*;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.지하철역_등록되어_있음;

@DisplayName("지하철 경로 검색")
public class PathSearchAcceptanceTest extends AcceptanceTest {

    private Long 이호선;
    private Long 신분당선;
    private Long 강남역;
    private Long 역삼역;
    private Long 선릉역;
    private Long 양재역;
    private Long 양재시민의숲역;

    @DisplayName("지하철역이 등록되어 있고, 노선이 등록되어 있고, 노선에 지하철역이 등록되어 있음")
    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        // given
        ExtractableResponse<Response> createLineResponse1 = 지하철_노선_등록되어_있음("2호선", "GREEN");
        ExtractableResponse<Response> createLineResponse2 = 지하철_노선_등록되어_있음("신분당선", "RED");
        ExtractableResponse<Response> createdStationResponse1 = 지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> createdStationResponse2 = 지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> createdStationResponse3 = 지하철역_등록되어_있음("선릉역");
        ExtractableResponse<Response> createdStationResponse4 = 지하철역_등록되어_있음("양재역");
        ExtractableResponse<Response> createdStationResponse5 = 지하철역_등록되어_있음("양재시민의숲");

        이호선 = createLineResponse1.as(LineResponse.class).getId();
        신분당선 = createLineResponse2.as(LineResponse.class).getId();
        강남역 = createdStationResponse1.as(StationResponse.class).getId();
        역삼역 = createdStationResponse2.as(StationResponse.class).getId();
        선릉역 = createdStationResponse3.as(StationResponse.class).getId();
        양재역 = createdStationResponse4.as(StationResponse.class).getId();
        양재시민의숲역 = createdStationResponse5.as(StationResponse.class).getId();

        지하철_노선에_지하철역_등록되어_있음(이호선, null, 강남역);
        지하철_노선에_지하철역_등록되어_있음(이호선, 강남역, 역삼역);
        지하철_노선에_지하철역_등록되어_있음(이호선, 역삼역, 선릉역);

        지하철_노선에_지하철역_등록되어_있음(신분당선, null, 강남역);
        지하철_노선에_지하철역_등록되어_있음(신분당선, 강남역, 양재역);
        지하철_노선에_지하철역_등록되어_있음(신분당선, 양재역, 양재시민의숲역);
    }

    @DisplayName("두 역의 최단 거리 경로를 조회")
    @Test
    void search_subway_path() {

        // when
        final ExtractableResponse<Response> pathSearchResponse =
                출발역에서_도착역까지의_최단_거리_경로_조회를_요청(선릉역, 양재시민의숲역);

        // then
        최단_거리_경로를_응답(pathSearchResponse);
        총_거리와_소요_시간을_함께_응답함(pathSearchResponse);
    }
}
