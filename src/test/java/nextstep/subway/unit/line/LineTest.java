package nextstep.subway.unit.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineLastStations;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionStations;
import nextstep.subway.station.domain.Station;

class LineTest {

    private final Station stationA = new Station(1L, "a");
    private final Station stationB = new Station(2L, "b");
    private final Station stationC = new Station(3L, "c");
    private final LineLastStations lastStations = new LineLastStations(stationA, stationB);

    @Test
    void updateName() {
        Line line = new Line("신분당선", "abc", lastStations, 1);

        line.updateName("신신분당선");

        assertThat(line.getName()).isEqualTo("신신분당선");
    }

    @Test
    void updateNameException() {
        Line line = new Line("신분당선", "abc", lastStations,1);
        assertThrows(CustomException.class, () -> line.updateName(""));
        assertThrows(CustomException.class, () -> line.updateName(null));
    }

    @Test
    void updateColor() {
        Line line = new Line("신분당선", "abc", lastStations, 1);

        line.updateColor("abcd");

        assertThat(line.getColor()).isEqualTo("abcd");
    }

    @Test
    void updateColorException() {
        Line line = new Line("신분당선", "abc", lastStations, 1);
        assertThrows(CustomException.class, () -> line.updateColor(""));
        assertThrows(CustomException.class, () -> line.updateColor(null));
    }

    @Test
    void addBaseSection() {
        Line line = new Line("신분당선", "abc", lastStations, 5);

        List<Section> sectionList = line.getSections();
        assertThat(sectionList).hasSize(1);
        assertThat(line.getDistance()).isEqualTo(5);
    }

    @Test
    void addSection() {
        Line line = new Line("신분당선", "abc", lastStations,3);

        Section section = new Section(line, new SectionStations(stationB, stationC), 3);
        line.addSection(section);

        List<Section> sections = line.getSections();
        LineLastStations stations = line.getLastStations();
        assertThat(sections).hasSize(2);
        assertThat(stations.getDownLastStation()).isEqualTo(stationC);
        assertThat(line.getDistance()).isEqualTo(6);
    }

    @Test
    void addSectionThrowException() {
        Line line = new Line("신분당선", "abc", lastStations,1);

        Section sectionA = new Section(line, new SectionStations(stationC, stationB), 3);
        Section sectionB = new Section(line, new SectionStations(stationB, stationA), 3);

        assertThrows(CustomException.class, () -> line.addSection(sectionA));
        assertThrows(CustomException.class, () -> line.addSection(sectionB));
    }

    @Test
    void deleteStation() {
        //given
        Line line = new Line("신분당선", "abc", lastStations, 1);
        Section section = new Section(line, new SectionStations(stationB, stationC), 3);
        line.addSection(section);

        //when
        line.deleteStation(stationC);

        //then
        List<Section> sections = line.getSections();
        LineLastStations stations = line.getLastStations();
        assertThat(sections).hasSize(1);
        assertThat(stations.getDownLastStation()).isEqualTo(stationB);
        assertThat(line.getDistance()).isEqualTo(1);
    }

    @Test
    void deleteStationExceptionWhenOnlyOneSection() {
        //given
        Line line = new Line("신분당선", "abc", lastStations, 1);

        assertThrows(CustomException.class, () -> line.deleteStation(stationB));
    }

    @Test
    void deleteStationExceptionWhenNotLastDownwardStation() {
        Line line = new Line("신분당선", "abc", lastStations, 1);
        Section section = new Section(line, new SectionStations(stationB, stationC), 3);
        line.addSection(section);

        assertThrows(CustomException.class, ()->line.deleteStation(stationB));
    }
}
