package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.path.acceptance.step.PathAcceptanceStep.출발역에서_도착역까지의_최단_거리_경로_조회_요청;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long lineId1;
    private Long lineId2;
    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private Long stationId5;

    @BeforeEach
    void beforeEach() {
        // given
        ExtractableResponse<Response> createLineResponse2 = 지하철_노선_등록되어_있음("신분당선", "RED");
        ExtractableResponse<Response> createLineResponse1 = 지하철_노선_등록되어_있음("2호선", "GREEN");
        ExtractableResponse<Response> createdStationResponse1 = 지하철역_등록되어_있음("양재시민의숲역");
        ExtractableResponse<Response> createdStationResponse2 = 지하철역_등록되어_있음("양재역");
        ExtractableResponse<Response> createdStationResponse3 = 지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> createdStationResponse4 = 지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> createdStationResponse5 = 지하철역_등록되어_있음("선릉역");

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
        지하철_노선에_지하철역_등록되어_있음(lineId2, null, stationId3);
        지하철_노선에_지하철역_등록되어_있음(lineId2, stationId3, stationId4);
        지하철_노선에_지하철역_등록되어_있음(lineId2, stationId4, stationId5);
    }

    @DisplayName("두 역의 최단 거리 경로를 조회")
    @Test
    void getShortestPath() {
        //when
        ExtractableResponse<Response> response = 출발역에서_도착역까지의_최단_거리_경로_조회_요청(stationId1, stationId5);

        //then
        //최단 거리 경로를 응답
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        PathResponse pathResponse = response.as(PathResponse.class);
        List<StationResponse> stations = pathResponse.getStations();
        assertThat(stations).extracting(StationResponse::getId)
                .containsExactlyElementsOf(Lists.list(stationId1, stationId2, stationId3, stationId4, stationId5));

        //총 거리와 소요 시간을 함께 응답함
        assertThat(pathResponse.getDistance()).isNotNull();
        assertThat(pathResponse.getDuration()).isNotNull();
    }


}
