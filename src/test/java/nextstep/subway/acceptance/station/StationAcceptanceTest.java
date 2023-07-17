package nextstep.subway.acceptance.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
        StationTestUtils.지하철역_생성(StationTestUtils.강남역_정보);

        // then
        노선_조회시_역이름을_조회할수_있다(StationTestUtils.지하철역_조회(), StationTestUtils.강남역_정보);
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
        StationTestUtils.지하철역_생성(StationTestUtils.강남역_정보);
        StationTestUtils.지하철역_생성(StationTestUtils.역삼역_정보);

        // when
        ExtractableResponse<Response> response = StationTestUtils.지하철역_조회();

        // then
        생성한_지하철_역이_모두_조회된다(response, 2);
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
        String stationUrl = StationTestUtils.지하철역_생성(StationTestUtils.강남역_정보);
        StationTestUtils.지하철역_생성(StationTestUtils.역삼역_정보);

        // when
        StationTestUtils.지하철역_삭제(stationUrl);

        // then
        역을_조회할수_없다(StationTestUtils.지하철역_조회(), StationTestUtils.강남역_정보);
    }

    private static void 생성한_지하철_역이_모두_조회된다(ExtractableResponse<Response> response, int targetCount) {
        List<String> stationNames = response.jsonPath().getList("name", String.class);

        assertThat(stationNames.size()).isEqualTo(targetCount);
    }

    private void 노선_조회시_역이름을_조회할수_있다(ExtractableResponse<Response> 지하철_조회_결과, Map<String, String> 역_정보) {
        assertThat(지하철_조회_결과.jsonPath().getList("name", String.class)).containsAnyOf(역_정보.get("name"));
    }

    private static void 역을_조회할수_없다(ExtractableResponse<Response> 지하철_조회_결과, Map<String, String> 역_정보) {
        assertThat(지하철_조회_결과.jsonPath().getList("name")).doesNotContain(역_정보.get("name"));
    }
}