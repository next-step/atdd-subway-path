package nextstep.subway.station.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.steps.LineSectionSteps.지하철_노선_생성_요청;
import static nextstep.subway.steps.LineSectionSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.steps.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성하면 지하철역 목록 조회 시 포함되어 있다.")
    @Test
    void createStation() {
        // when
        var response = 지하철역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        지하철역이_존재한다("강남역");
    }

    @DisplayName("지하철역을 조회하면 존재하는 모든 지하철역을 응답 받는다.")
    @Test
    void getStations() {
        // given
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("역삼역");

        // when + then
        지하철역이_존재한다("강남역", "역삼역");
    }

    @DisplayName("지하철역을 제거하면 목록 조회 시 조회되지 않는다.")
    @Test
    void deleteStation() {
        // given
        Long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");

        // when
        지하철역_삭제_요청(강남역);

        // then
        지하철역이_존재하지_않는다("강남역");
    }

    @DisplayName("지하철역이 속한 노선이 존재하면 지하철역을 제거할 수 없다")
    @Test
    void deleteStation_Exception() {
        // given
        Long 신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");
        Long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 교대역));

        // when + then
        지하철역_제거에_실패한다(강남역);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }

    private void 지하철역이_존재한다(String... names) {
        List<String> stationNames = 지하철역_목록_조회_요청().jsonPath().getList("name", String.class);

        assertThat(stationNames).contains(names);
    }

    private void 지하철역이_존재하지_않는다(String... names) {
        List<String> stationNames = 지하철역_목록_조회_요청().jsonPath().getList("name", String.class);

        assertThat(stationNames).doesNotContain(names);
    }

    private void 지하철역_제거에_실패한다(Long stationId) {
        ExtractableResponse<Response> response = 지하철역_삭제_요청(stationId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}