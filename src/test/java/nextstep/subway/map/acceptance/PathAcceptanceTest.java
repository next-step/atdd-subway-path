package nextstep.subway.map.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.map.acceptance.step.MapAcceptanceStep.*;
import static nextstep.subway.map.acceptance.step.MapAcceptanceStep.지하철_노선도에_노선별_지하철역_순서_정렬됨;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.지하철역_등록되어_있음;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {

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
        ExtractableResponse<Response> createLineResponse2 = 지하철_노선_등록되어_있음("신분당성", "RED");
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

    @DisplayName("두 역의 최단 거리 경로를 조회")
    @Test
    void shortestPath() {
        // when
        ExtractableResponse<Response> response = 출발역에서_도착역까지의_최단거리_경로_조회를_요청(1L, 2L);
    }

    private ExtractableResponse<Response> 출발역에서_도착역까지의_최단거리_경로_조회를_요청(Long sourceId, Long targetId) {
        return RestAssured
                .given()
                    .log().all()
                    .accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                    get("/paths?source={sourceId}&target={targetId}", sourceId, targetId).
                then().
                    log().all().
                extract();
    }
}
