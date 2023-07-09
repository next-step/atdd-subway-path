package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        // given
        Line line = new Line("신분당선", "reg-bg-500");
        Station upStation = new Station("지하철역");
        Station downStation = new Station("또다른지하철역");

        // when
        line.addSection(new Section(line, upStation, downStation, 10));

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    void getStations() {
        // given
        Line line = new Line("신분당선", "reg-bg-500");
        Station upStation = new Station("지하철역");
        Station downStation = new Station("또다른지하철역");

        // when
        line.addSection(new Section(line, upStation, downStation, 10));

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @Test
    void removeSection() {
        // given
        Line line = new Line("신분당선", "reg-bg-500");
        Station upStation = new Station("지하철역");
        Station downStation = new Station("또다른지하철역");
        line.addSection(new Section(line, upStation, downStation, 10));

        // when
        line.removeSection();

        // then
        assertThat(line.getSections().size()).isEqualTo(0);
    }
}
