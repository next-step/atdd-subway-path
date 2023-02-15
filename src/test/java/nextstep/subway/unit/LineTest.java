package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line line = new Line("2호선", "green");
    private Station upStation = new Station("당산역");
    private Station downStation = new Station("신도림역");

    @Test
    void addSection() {
        // given
        Section section = getSection();

        // when
        line.addSection(section);

        // then
        assertThat(line.getSectionsCount()).isEqualTo(1);

        Section firstSection = line.getFirstSection();
        assertThat(firstSection.getUpStation().getName()).isEqualTo(upStation.getName());
        assertThat(firstSection.getDownStation().getName()).isEqualTo(downStation.getName());
    }

    @Test
    void getStations() {
        // given
        Section section = getSection();

        // when
        line.addSection(section);

        // then
        List<String> stationNames = line.getStations().stream().map(Station::getName).collect(Collectors.toList());
        assertThat(stationNames).contains(upStation.getName(), downStation.getName());
    }

    @Test
    void removeSection() {
        // given
        Section section = getSection();
        line.addSection(section);

        // when
        line.removeSection(section.getDownStation());

        // then
        assertThat(line.getSectionsCount()).isEqualTo(0);
    }

    private Section getSection() {
        return new Section(line, upStation, downStation, 20);
    }

}
