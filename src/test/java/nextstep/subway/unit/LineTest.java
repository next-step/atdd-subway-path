package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    @Test
    void addSection() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = new Section(line, new Station("지하철역1"), new Station("지하철역2"), 5);
        line.addSection(section);

        assertThat(line.getSections().contains(section)).isEqualTo(true);
    }

    @Test
    void getStations() {
        Station station = new Station("지하철역1");
        Station station2 = new Station("지하철역2");
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = new Section(line, station, station2, 5);
        line.addSection(section);

        List<Station> stations = line.getStations();

        assertAll(
                () -> assertThat(stations.get(0).getName()).isEqualTo("지하철역1"),
                () -> assertThat(stations.get(1).getName()).isEqualTo("지하철역2"));
    }

    @Test
    void removeSection() throws Exception{
        Station station = new Station("지하철역1");
        Station station2 = new Station("지하철역2");
        Station station3 = new Station("지하철역3");
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = new Section(line, station, station2, 5);
        Section section2 = new Section(line, station2, station3, 10);
        line.addSection(section);
        line.addSection(section2);

        line.removeSection(station3);

        assertThat(line.getStations().stream().anyMatch(a -> a.getName().equals(station3.getName()))).isEqualTo(false);
    }
}
