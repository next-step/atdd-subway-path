package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.common.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.acceptance.common.util.AcceptanceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@AcceptanceTest
public class StationAcceptanceTest {
    final Station.RequestBody 강남역 = Station.랜덤_REQUEST_BODY();
    final Station.RequestBody 역삼역 = Station.랜덤_REQUEST_BODY();

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = Station.Api.createStationBy(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(Station.Api.listStationName()).containsExactly(강남역.name);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("모든 지하철역 목록을 조회한다.")
    @Test
    void showStations() {
        // given
        Station.Api.createStationBy(강남역);
        Station.Api.createStationBy(역삼역);

        // when
        ExtractableResponse<Response> response = Station.Api.listStation();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<String> stationNames = response.jsonPath().getList("name", String.class);
        assertThat(stationNames).containsExactly(강남역.name, 역삼역.name);
    }


    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역 목록을 삭제한다.")
    @Test
    void deleteStation() {
        // given
        final Long 강남역_ID = Station.Api.createStationBy(강남역).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = Station.Api.deleteStationBy(강남역_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        assertThat(Station.Api.listStationName()).doesNotContain(강남역.name);
    }


}