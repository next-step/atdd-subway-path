package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.step_feature.StationStepFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.step_feature.StationStepFeature.강남역_이름;
import static nextstep.subway.acceptance.step_feature.StationStepFeature.판교역_이름;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관리 기능")
class StationAcceptanceTest extends AcceptanceTest {

    private Map<String, String> params;
    private Map<String, String> params2;

    @BeforeEach
    void setUpStation() {
        params = StationStepFeature.createStationParams(강남역_이름);
        params2 = StationStepFeature.createStationParams(판교역_이름);
    }

    /**
     * When 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 성공한다.
     */
    @DisplayName("지하철역 생성")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = StationStepFeature.callCreateStation(params);

        // then
        StationStepFeature.checkCreateStation(response);
    }

    /**
     * Given 지하철역 생성
     * When 같은 이름으로 지하철역 생성
     * Then 400 status code를 응답한다.
     */
    @DisplayName("중복 이름의 지하철역을 생성하면 실패한다")
    @Test
    void createStation_duplicate_fail() {
        // given
        StationStepFeature.callCreateStation(params);

        // when
        ExtractableResponse<Response> response = StationStepFeature.callCreateStation(params);

        // then
        StationStepFeature.checkCreateStationFail(response);
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 생성한 지하철을 조회한다.
     * Then 생성한 지하철의 정보를 응답받는다
     */
    @DisplayName("지하철역 id로 조회")
    @Test
    void showStation() {
        // given
        ExtractableResponse<Response> createResponse = StationStepFeature.callCreateStation(params);
        String location = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = StationStepFeature.callFindStationByUri(location);

        // then
        StationStepFeature.checkFindStation(response);

        String stationName = response.jsonPath().getString("name");
        assertThat(stationName).isEqualTo(강남역_이름);
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * Given 새로운 지하철역 생성을 요청 하고
     * When 지하철역 목록 조회를 요청 하면
     * Then 두 지하철역이 포함된 지하철역 목록을 응답받는다
     */
    @DisplayName("지하철역 목록 조회")
    @Test
    void getStations() {
        // given
        StationStepFeature.callCreateStation(params);
        StationStepFeature.callCreateStation(params2);

        // when
        ExtractableResponse<Response> response = StationStepFeature.callFindAllStation();

        // then
        StationStepFeature.checkFindStation(response);

        List<String> stationNames = response.jsonPath().getList("name");
        assertThat(stationNames).contains(강남역_이름, 판교역_이름);
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 생성한 지하철역 삭제를 요청 하면
     * Then 생성한 지하철역 삭제가 성공한다.
     */
    @DisplayName("지하철역 삭제")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = StationStepFeature.callCreateStation(params);
        String location = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = StationStepFeature.callDeleteStation(location);

        // then
        StationStepFeature.checkResponseStatus(response.statusCode(), HttpStatus.NO_CONTENT);
    }

}
