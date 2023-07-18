package nextstep.subway.station;

import common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static common.Constants.강남역;
import static common.Constants.신논현역;
import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.station.StationTestStepDefinition.*;

@DisplayName("지하철역 관련 기능")
@AcceptanceTest
public class StationAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        지하철_역_생성_요청(강남역);

        // then
        var stationNames = 지하철_역_이름_목록_조회_요청();

        assertThat(stationNames).containsAnyOf(강남역);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다")
    @Test
    void getStationList() {
        // when
        지하철_역_생성_요청(강남역);
        지하철_역_생성_요청(신논현역);

        // given
        var stationNames = 지하철_역_이름_목록_조회_요청();

        // then
        assertThat(stationNames).containsExactly(강남역, 신논현역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제 후 확인한다")
    @Test
    void deleteStation() {
        // when
        var response = 지하철_역_생성_요청(강남역);

        // given
        지하철_역_삭제_요청(response.getId());

        // then
        var stationNames = 지하철_역_목록_조회_요청();

        assertThat(stationNames).isEmpty();
    }
}