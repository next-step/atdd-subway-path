package subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.acceptance.apiTester.SubwayLineAcceptanceTest;
import subway.common.annotation.AcceptanceTest;

/**
 * 지하철 노선 조회 인수 테스트를 합니다.
 */
@AcceptanceTest
@DisplayName("지하철 노선 조회 인수 테스트")
public class SubwayLineQueryAcceptanceTest extends SubwayLineAcceptanceTest {

    /**
     * Given 지하철 노선을 생성하고<br>
     * When 생성한 지하철 노선을 조회하면<br>
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.<br>
     */
    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void getSubwayLine() {
        //given
        지하철_노선_생성("2호선","bg-green-600","교대역", "강남역", 10);

        //when
        ExtractableResponse<Response> response = 지하철_노선_상세_조회("2호선");

        //then
        지하철_노선_상세_조회_응답_비교(response, "2호선", "bg-green-600", "교대역", "강남역");
    }
}
