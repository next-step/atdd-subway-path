package nextstep.subway.map.acceptance;

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
import static nextstep.subway.map.acceptance.step.MapAcceptanceStep.*;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.지하철역_등록되어_있음;
import static nextstep.util.RestAssuredUtils.ETag를_검증한다;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class MapAcceptanceTest extends AcceptanceTest {
    private Long 이호선;
    private Long 신분당선;
    private Long 강남역;
    private Long 역삼역;
    private Long 선릉역;
    private Long 양재역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> createLineResponse1 = 지하철_노선_등록되어_있음("2호선", "GREEN");
        ExtractableResponse<Response> createLineResponse2 = 지하철_노선_등록되어_있음("신분당선", "RED");
        ExtractableResponse<Response> createdStationResponse1 = 지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> createdStationResponse2 = 지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> createdStationResponse3 = 지하철역_등록되어_있음("선릉역");
        ExtractableResponse<Response> createdStationResponse4 = 지하철역_등록되어_있음("양재역");

        이호선 = createLineResponse1.as(LineResponse.class).getId();
        신분당선 = createLineResponse2.as(LineResponse.class).getId();
        강남역 = createdStationResponse1.as(StationResponse.class).getId();
        역삼역 = createdStationResponse2.as(StationResponse.class).getId();
        선릉역 = createdStationResponse3.as(StationResponse.class).getId();
        양재역 = createdStationResponse4.as(StationResponse.class).getId();

        지하철_노선에_지하철역_등록되어_있음(이호선, null, 강남역);
        지하철_노선에_지하철역_등록되어_있음(이호선, 강남역, 역삼역);
        지하철_노선에_지하철역_등록되어_있음(이호선, 역삼역, 선릉역);
        지하철_노선에_지하철역_등록되어_있음(신분당선, null, 강남역);
        지하철_노선에_지하철역_등록되어_있음(신분당선, 강남역, 양재역);
    }

    @DisplayName("지하철 노선도를 조회한다.")
    @Test
    void loadMap() {

        // when
        final ExtractableResponse<Response> searchResponse = 지하철_노선도_조회_요청();

        // then
        지하철_노선도_조회_응답됨(searchResponse);
        지하철_노선도에_노선별_지하철역_순서_정렬됨(searchResponse, 이호선, 강남역, 역삼역, 선릉역);
        지하철_노선도에_노선별_지하철역_순서_정렬됨(searchResponse, 신분당선, 강남역, 양재역);
    }

    @DisplayName("캐시 적용을 검증한다.")
    @Test
    void loadMapWithETag() {

        // given
        final ExtractableResponse<Response> searchResponse = 지하철_노선도_조회_요청();
        ETag를_검증한다("/maps", searchResponse);
    }
}
