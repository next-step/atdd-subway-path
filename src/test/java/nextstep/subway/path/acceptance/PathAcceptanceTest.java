package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long lineId1;
    private Long lineId2;
    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private Long stationId5;

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
        ExtractableResponse<Response> createdStationResponse5 = 지하철역_등록되어_있음("양재시민의숲");

        lineId1 = createLineResponse1.as(LineResponse.class).getId();
        lineId2 = createLineResponse2.as(LineResponse.class).getId();

        stationId1 = createdStationResponse1.as(StationResponse.class).getId();
        stationId2 = createdStationResponse2.as(StationResponse.class).getId();
        stationId3 = createdStationResponse3.as(StationResponse.class).getId();
        stationId4 = createdStationResponse4.as(StationResponse.class).getId();
        stationId5 = createdStationResponse5.as(StationResponse.class).getId();

        지하철_노선에_지하철역_등록되어_있음(lineId1, null, stationId1);
        지하철_노선에_지하철역_등록되어_있음(lineId1, stationId1, stationId2);
        지하철_노선에_지하철역_등록되어_있음(lineId1, stationId2, stationId3);

        지하철_노선에_지하철역_등록되어_있음(lineId2, null, stationId1);
        지하철_노선에_지하철역_등록되어_있음(lineId2, stationId1, stationId4);
        지하철_노선에_지하철역_등록되어_있음(lineId2, stationId4, stationId5);
    }

    @DisplayName("두 역의 최단 거리 경로를 조회")
    @Test
    public void findShortestPath() {
        // given

        // when
        // 출발역에서 도착역까지의 최단 거리 경로 조회를 요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .queryParam("startStationId", stationId3)
                .queryParam("endStationId", stationId5)
                .get("/paths")
                .then()
                .log()
                .all()
                .extract();

        // then
        // 최단 거리 경로를 응답
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getStations()).isNotEmpty();

        // 총 거리와 소요 시간을 함께 응답함
        assertThat(pathResponse.getWeight()).isNotNull();
        // 역간은 모두 duration : 5.
        assertThat(pathResponse.getWeight()).isEqualTo(20);
    }
}
