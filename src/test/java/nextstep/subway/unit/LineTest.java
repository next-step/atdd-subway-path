package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.LineTest.Stub.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련")
class LineTest {

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        // given
        Line line = new Line("2호선", "green");

        // when
        line.addSection(구로디지털단지역, 신대방역, 10);

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @DisplayName("지하철 노선에 등록된 지하철역 조회")
    @Test
    void getStations() {
        // given
        Line line = new Line("2호선", "green");

        // when
        line.addSection(구로디지털단지역, 신대방역, 10);
        line.addSection(신대방역, 신림역, 8);

        // then
        assertThat(line.getStations()).contains(구로디지털단지역, 신대방역, 신림역);
    }

    @Test
    void removeSection() {
    }

    protected static class Stub {
        public static final Station 구로디지털단지역 = new Station("구로디지털단지역");
        public static final Station 신대방역 = new Station("신대방역");
        public static final Station 신림역 = new Station("신림역");
    }
}
