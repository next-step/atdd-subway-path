package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련")
class LineTest {

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        // given
        Station 구로디지털단지역 = new Station("구로디지털단지역");
        Station 신대방역 = new Station("신대방역");
        Line line = new Line("2호선", "green");

        // when
        line.addSection(구로디지털단지역, 신대방역, 10);

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
