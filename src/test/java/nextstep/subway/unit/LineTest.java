package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    @DisplayName("새로운 구간을 등록하면 신규 등록한 구간의 지하철역들을 찾을 수 있다.")
    void addSection() {
        //given
        Line line = new Line("2호선", "bg-green-600");
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        final int distance = 10;

        // when
        line.addSection(upStation, downStation, distance);

        // then
        assertThat(line.getStations()).containsOnly(upStation, downStation);
    }

    @Test
    void removeSection() {
    }
}
