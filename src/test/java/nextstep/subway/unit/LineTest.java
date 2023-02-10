package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    @Test
    void addSection() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = new Section(line, new Station("지하철역1"), new Station("지하철역3"), 5);
        line.addSection(section);

        assertThat(line.getSections().contains(section)).isEqualTo(true);
    }

    @Test
    void addSection_front() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section oldSection = new Section(line, new Station("지하철역1"), new Station("지하철역3"), 5);
        line.addSection(oldSection);

        Section newSection = new Section(line, new Station("지하철역0"), new Station("지하철역1"), 5);
        line.addSection("1", newSection);

        assertThat(line.getStations().stream().map(Station::getName)).containsExactly("지하철역0", "지하철역1", "지하철역3");
    }

    @Test
    void addSection_middle() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section oldSection = new Section(line, new Station("지하철역1"), new Station("지하철역3"), 7);
        line.addSection(oldSection);

        Section newSection = new Section(line, new Station("지하철역1"), new Station("지하철역2"), 5);
        line.addSection("2", newSection);

        assertThat(line.getStations().stream().map(Station::getName)).containsExactly("지하철역1", "지하철역2", "지하철역3");
        assertThat(line.getSections().stream().map(Section::getDistance)).containsExactly(5, 2);
    }

    @Test
    void addSection_middle_Exception1() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section oldSection = new Section(line, new Station("지하철역1"), new Station("지하철역3"), 7);
        line.addSection(oldSection);

        Section newSection = new Section(line, new Station("지하철역1"), new Station("지하철역2"), 7);

        assertThrows(IllegalArgumentException.class, () -> line.addSection("2", newSection));
    }

    @Test
    void addSection_middle_Exception2() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section oldSection = new Section(line, new Station("지하철역1"), new Station("지하철역3"), 7);
        line.addSection(oldSection);

        Section newSection = new Section(line, new Station("지하철역1"), new Station("지하철역3"), 7);

        assertThrows(IllegalArgumentException.class, () -> line.addSection(newSection));
    }

    @Test
    void addSection_middle_Exception3() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section oldSection = new Section(line, new Station("지하철역1"), new Station("지하철역3"), 7);
        line.addSection(oldSection);

        Section newSection = new Section(line, new Station("지하철역4"), new Station("지하철역5"), 7);

        assertThrows(IllegalArgumentException.class, () -> line.addSection(newSection));
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
