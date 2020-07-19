package nextstep.subway.map.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.map.dto.MapResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.지하철역_등록되어_있음;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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

    @DisplayName("지하철 노선도를 조회한다.")
    @Test
    void loadMap() {
        //Given
        Map<Long, ArrayList<Long>> maps = new HashMap<>();
        maps.put(lineId1, new ArrayList<Long>(Arrays.asList(stationId1, stationId2, stationId3)));
        maps.put(lineId2, new ArrayList<Long>(Arrays.asList(stationId1, stationId4)));

        //When 
        ExtractableResponse<Response> response = 지하철_노선도_조회_요청();

        //Then 
        지하철_노선도_응답됨(response);
        //And 
        지하철_노선도에_노선별_지하철역_순서_정렬됨(response, maps);
    }

    private void 지하철_노선도에_노선별_지하철역_순서_정렬됨(ExtractableResponse<Response> response, Map<Long, ArrayList<Long>> expectedMaps) {
        Map<Long, ArrayList<Long>> maps = new HashMap<>();

        List <LineResponse> lines = response.as(MapResponse.class).getLineResponses();

        for (final LineResponse line: lines) {
            maps.put(
                 line.getId()
                , (ArrayList<Long>) line.getStations().stream().map(c -> c.getStation().getId())
                            .collect(Collectors.toList())
            );
        }
        assertThat(maps).containsAllEntriesOf(expectedMaps);
    }

    private void 지하철_노선도_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선도_조회_요청() {
        return RestAssured.given().log().all().
        accept(MediaType.APPLICATION_JSON_VALUE).
        when().
        get("/maps").
        then().
        log().all().
        extract();
    }

    @DisplayName("캐시 적용을 검증한다.")
    @Test
    void loadMapWithETag() {
    }
}
