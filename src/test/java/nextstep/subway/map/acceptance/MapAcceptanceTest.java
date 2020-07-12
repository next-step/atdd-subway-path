package nextstep.subway.map.acceptance;

import com.google.common.collect.Lists;
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

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class MapAcceptanceTest extends AcceptanceTest {
    private Long lineId1;
    private Long lineId2;
    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;

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

        lineId1 = createLineResponse1.as(LineResponse.class).getId();
        lineId2 = createLineResponse2.as(LineResponse.class).getId();
        stationId1 = createdStationResponse1.as(StationResponse.class).getId();
        stationId2 = createdStationResponse2.as(StationResponse.class).getId();
        stationId3 = createdStationResponse3.as(StationResponse.class).getId();
        stationId4 = createdStationResponse4.as(StationResponse.class).getId();

        지하철_노선에_지하철역_등록되어_있음(lineId1, null, stationId1);
        지하철_노선에_지하철역_등록되어_있음(lineId1, stationId1, stationId2);
        지하철_노선에_지하철역_등록되어_있음(lineId1, stationId2, stationId3);
        지하철_노선에_지하철역_등록되어_있음(lineId2, null, stationId1);
        지하철_노선에_지하철역_등록되어_있음(lineId2, stationId1, stationId4);
    }

    @DisplayName("지하철 노선도를 조회한다.")
    @Test
    void loadMap() {
        // when
        ExtractableResponse<Response> mapResponse = 지하철_노선도_캐시_조회_요청();

        // then
        지하철_노선도_응답됨(mapResponse);
        지하철_노선도에_노선별_지하철역_순서_정렬됨(mapResponse, lineId1, Lists.newArrayList(stationId1, stationId2, stationId3));
        지하철_노선도에_노선별_지하철역_순서_정렬됨(mapResponse, lineId2, Lists.newArrayList(stationId1, stationId4));
    }

    @DisplayName("캐시 적용을 검증한다.")
    @Test
    void loadMapWithETag() {
        // when
        ExtractableResponse<Response> mapResponse = 지하철_노선도_캐시_조회_요청();

        // then
        지하철_노선도_조회_시_ETag_포함됨(mapResponse);

        // when
        ExtractableResponse<Response> mapResponseWithCache = 지하철_노선도_캐시_조회_요청(mapResponse.header("ETag"));

        // then
        지하철_노선도_조회_캐시_적용됨(mapResponseWithCache);
    }
}
