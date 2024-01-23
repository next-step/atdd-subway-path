package nextstep.subway.acceptance.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.acceptance.annotation.AcceptanceTest;
import nextstep.subway.utils.JsonPathUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@AcceptanceTest
public class StationAcceptanceTest {
    
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        //given
        String stationName = "강남역";

        // when
        ExtractableResponse<Response> response = StationApiRequester.createStationApiCall(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = JsonPathUtil.getNames(StationApiRequester.showStationsApiCall());
        assertThat(stationNames).containsAnyOf(stationName);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("생성한 지하철역 목록 조회")
    @Test
    void showStations() {
        //given
        String 성수역 = "성수역";
        StationApiRequester.createStationApiCall(성수역);
        String 잠실역 = "잠실역";
        StationApiRequester.createStationApiCall(잠실역);

        //when
        ExtractableResponse<Response> response = StationApiRequester.showStationsApiCall();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(JsonPathUtil.getNames(response)).containsExactly(성수역, 잠실역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("생성한 지하철역 삭제")
    @Test
    void deleteStation() {
        //given
        String 언주역 = "언주역";
        Long 언주역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall(언주역));

        //when
        ExtractableResponse<Response> response = StationApiRequester.deleteStationApiCall(언주역id);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        List<String> stationsNames = JsonPathUtil.getNames(StationApiRequester.showStationsApiCall());
        assertThat(stationsNames).isEmpty();
    }
}