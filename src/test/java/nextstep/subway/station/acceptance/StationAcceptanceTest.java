package nextstep.subway.station.acceptance;

import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.station.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        var response = 지하철역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        지하철역이_존재한다("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("역삼역");

        // when + then
        지하철역이_존재한다("강남역", "역삼역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");

        // when
        지하철역_삭제_요청(강남역);

        // then
        지하철역이_존재하지_않는다("강남역");
    }

    private void 지하철역이_존재한다(String... names) {
        List<String> stationNames = 지하철역_목록_조회_요청().jsonPath().getList("name", String.class);

        assertThat(stationNames).contains(names);
    }

    private void 지하철역이_존재하지_않는다(String... names) {
        List<String> stationNames = 지하철역_목록_조회_요청().jsonPath().getList("name", String.class);

        assertThat(stationNames).doesNotContain(names);
    }
}