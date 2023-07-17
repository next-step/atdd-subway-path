package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static nextstep.subway.station.StationTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {


    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        지하철역_생성(강남역_정보);

        // then
        역_생성_여부_검증(지하철역_조회(), 강남역_정보);
    }


    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void selectStations() {
        // given
        지하철역_생성(강남역_정보);
        지하철역_생성(역삼역_정보);

        // when
        ExtractableResponse<Response> response = 지하철역_조회();

        // then
        역_개수_검증(response, 2);
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
        String stationUrl = 지하철역_생성(강남역_정보);
        지하철역_생성(역삼역_정보);

        // when
        지하철역_삭제(stationUrl);

        // then
        역_삭제_여부_검증(지하철역_조회(), 강남역_정보);
    }

    private static void 역_개수_검증(ExtractableResponse<Response> response, int targetCount) {
        List<String> stationNames = response.jsonPath().getList("name", String.class);

        assertThat(stationNames.size()).isEqualTo(targetCount);
    }

    private void 역_생성_여부_검증(ExtractableResponse<Response> 지하철_조회_결과, Map<String, String> 역_정보) {
        assertThat(지하철_조회_결과.jsonPath().getList("name", String.class)).containsAnyOf(역_정보.get("name"));
    }

    private static void 역_삭제_여부_검증(ExtractableResponse<Response> 지하철_조회_결과, Map<String, String> 역_정보) {
        assertThat(지하철_조회_결과.jsonPath().getList("name")).doesNotContain(역_정보.get("name"));
    }
}