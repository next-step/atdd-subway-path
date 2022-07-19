package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("새로운 구간을 정상적으로 추가했다")
    @Test
    void addSection() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Line line = new Line("9호선", "bg-brown-600");

        // when
        line.addSection(upStation, downStation, 5);

        // then
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
