package nextstep.subway.acceptance.station;

import static org.assertj.core.api.Assertions.assertThat;

import static nextstep.subway.acceptance.station.StationSteps.모든_지하철역을_조회한다;
import static nextstep.subway.acceptance.station.StationSteps.지하철역을_생성한다;
import static nextstep.subway.acceptance.station.StationSteps.지하철역을_제거한다;
import static nextstep.subway.acceptance.station.StationSteps.지하철역을_조회한다;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.acceptance.AcceptanceTest;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        final String name = "강남역";

        // when
        지하철역을_생성한다(name);

        // then
        assertThat(지하철역을_조회한다(name)).hasSize(1);
    }

    /**
     * Given n개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then n개의 지하철역을 응답 받는다
     */
    @DisplayName("모든 지하철역 목록을 조회한다.")
    @Test
    void showStations() {
        final List<String> names = List.of("강남역", "역삼역");

        // given
        지하철역을_생성한다(names);

        // when
        final var responses = 모든_지하철역을_조회한다();

        // then
        assertThat(responses).hasSize(names.size());
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        final String name = "강남역";

        // given
        final Long stationId = 지하철역을_생성한다(name).getId();

        // when
        지하철역을_제거한다(stationId);

        // then
        assertThat(모든_지하철역을_조회한다()).isEmpty();
    }
}