package subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.acceptance.apiTester.StationAcceptanceTest;
import subway.common.annotation.AcceptanceTest;

/**
 * 지하철역 생성 인수 테스트를 합니다.
 */
@AcceptanceTest
@DisplayName("지하철역 생성 인수 테스트")
public class StationRegisterAcceptanceTest extends StationAcceptanceTest {
    /**
     * When 지하철역을 생성하면<br>
     * Then 지하철역이 생성된다<br>
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다<br>
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성("강남역");

        // then
        생성_성공_확인(response);

        // then
        지하철역_목록_포함_여부_확인("강남역");
    }

}
