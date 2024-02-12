package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.station.dto.StationRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends BaseTest {

    private static final String STATION_API_PATH = "/stations";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철역_생성() {
        // when
        final StationRequest request = new StationRequest("강남역");
        final ExtractableResponse<Response> response = callPostApi(request, STATION_API_PATH);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        final ExtractableResponse<Response> 지하철역_조회_요청 = callGetApi(STATION_API_PATH);
        final JsonPath jsonPath = 지하철역_조회_요청.jsonPath();
        final List<String> stationNames = jsonPath.getList("name", String.class);

        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철 목록을 조회한다.")
    @Test
    void 지하철역_목록_조회() {
        // given
        final StationRequest 교대역_생성_요청 = new StationRequest("교대역");
        callPostApi(교대역_생성_요청, STATION_API_PATH);

        final StationRequest 역삼역_생성_요청 = new StationRequest("역삼역");
        callPostApi(역삼역_생성_요청, STATION_API_PATH);

        // when
        final ExtractableResponse<Response> 지하철역_조회_요청 = callGetApi(STATION_API_PATH);
        final JsonPath jsonPath = 지하철역_조회_요청.jsonPath();

        // then
        final List<String> stationNames = jsonPath.getList("name", String.class);

        assertThat(stationNames).containsExactly("교대역", "역삼역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void 지하철역_삭제() {
        // given
        final String stationName = "강남역";
        final StationRequest request = new StationRequest(stationName);
        final ExtractableResponse<Response> createStationResponse = callPostApi(request, STATION_API_PATH);

        // when
        final Long stationId = getIdFromApiResponse(createStationResponse);
        callDeleteApi(STATION_API_PATH + "/{id}", stationId);

        // then
        final ExtractableResponse<Response> 지하철역_조회_요청 = callGetApi(STATION_API_PATH);
        final JsonPath jsonPathAfterStationDeletion = 지하철역_조회_요청.jsonPath();
        final List<String> stationNames = jsonPathAfterStationDeletion.getList("name", String.class);

        assertThat(stationNames).doesNotContain("강남역");
    }

}
