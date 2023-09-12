package subway.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.acceptance.apiTester.StationAcceptanceTest;
import subway.acceptance.utils.AcceptanceTest;

/**
 * 지하철역 비활성화 인수 테스트를 합니다.
 */
@AcceptanceTest
@DisplayName("지하철역 비활성화 인수 테스트")
public class StationCloseAcceptanceTest extends StationAcceptanceTest {

    /**
     * Given 지하철역을 생성하고<br>
     * When 그 지하철역을 비활성화하면<br>
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다<br>
     */
    @Test
    @DisplayName("지하철역을 비활성화한다.")
    void closeStation() {
        //given
        지하철_역_생성("강남역");
        //when
        지하철역_비활성화("강남역");
        //then
        지하철역_목록_미포함_여부_확인("강남역");
    }
}
