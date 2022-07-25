package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    void addSection() {
        Line line = new Line();
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");

        line.addSection(upStation, downStation, 10);

        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    void getStations() {
        Line line = new Line();
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        line.addSection(upStation, downStation, 10);

        List<Station> stations = line.getStations();

        assertThat(stations).containsExactly(upStation, downStation);
    }

    @Test
    void removeSection() {
    }
}
