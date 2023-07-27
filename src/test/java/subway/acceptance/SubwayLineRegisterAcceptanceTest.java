package subway.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.acceptance.apiTester.SubwayLineAcceptanceTest;
import subway.acceptance.utils.AcceptanceTest;

/**
 * 지하철 노선 등록 인수 테스트를 합니다.
 */
@AcceptanceTest
@DisplayName("지하철 노선 등록 인수 테스트")
public class SubwayLineRegisterAcceptanceTest extends SubwayLineAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면<br>
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다<br>
     */
    @Test
    @DisplayName("지하철 노선 등록을 한다")
    void registerSubwayLine() {
        //when
        지하철_노선_생성("2호선","bg-green-600","교대역", "강남역", 10);
        //then
        지하철_노선_목록_포함_여부_확인("2호선");
    }
}
