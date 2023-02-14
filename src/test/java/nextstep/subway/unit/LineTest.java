package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("2호선", "green", 강남역, 정자역, 10);
        line.addSection(정자역, 광교역, 15);
        assertThat(line.getSections()).containsExactly(
                new Section(line, 강남역, 정자역, 10),
                new Section(line, 정자역, 광교역, 15)
        );
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
