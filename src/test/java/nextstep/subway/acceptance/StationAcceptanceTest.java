package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.StationAcceptanceAssert.지하철역_생성_요청_검증;
import static nextstep.subway.acceptance.StationAcceptanceAssert.지하철역_제거_요청_검증;
import static nextstep.subway.acceptance.StationAcceptanceAssert.지하철역_조회_요청_검증;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_조회_요청;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        지하철역_생성_요청_검증(response, "강남역");
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

        // when
        var stationResponse = 지하철역_조회_요청();

        // then
        지하철역_조회_요청_검증(stationResponse, 2);
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
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청("강남역");

        // when
        지하철역_제거_요청(createResponse);

        // then
        지하철역_제거_요청_검증("강남역");
    }
}
