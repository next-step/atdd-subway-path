package subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.acceptance.apiTester.SubwayLineAcceptanceTest;
import subway.common.annotation.AcceptanceTest;

/**
 * 지하철 노선 목록 조회 인수 테스트를 합니다.
 */
@AcceptanceTest
@DisplayName("지하철 노선 목록 조회 인수 테스트")
public class SubwayLineListQueryAcceptanceTest extends SubwayLineAcceptanceTest {

    /**
     * Given 2개의 지하철 노선을 생성하고<br>
     * When 지하철 노선 목록을 조회하면<br>
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.<br>
     */
    @Test
    @DisplayName("지하철 노선 목록 조회를 한다")
    void getSubwayLines() {
        //given
        지하철_노선_생성("2호선","bg-green-600","교대역", "강남역", 10);
        지하철_노선_생성("1호선","bg-blue-600","동인천", "신도림", 80);

        //when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        //then
        지하철_노선_목록_포함_여부_확인(response, "2호선", "1호선");
    }

}
