package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LineTest {
    @Test
    void addSection() {
        // given
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        Line line = new Line("2호선", "green");
        Section section = new Section(line, upStation, downStation, 10);

        // when - then
        assertDoesNotThrow(() -> line.addSection(section));
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    void getStations() {
        // given
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        Line line = new Line("2호선", "green");
        Section section = new Section(line, upStation, downStation, 10);

        // when
        line.addSection(section);

        // then
        assertThat(line.getAllStations().size()).isEqualTo(2);
        assertThat(line.getAllStations().stream().map(Station::getName).collect(Collectors.toList())).contains("강남역", "교대역");
    }

    @Test
    void removeSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        Line line = new Line("2호선", "green");
        Section section1 = new Section(line, 강남역, 교대역, 10);
        Section section2 = new Section(line, 교대역, 서초역, 10);
        line.addSection(section1);
        line.addSection(section2);

        // when
        line.removeSection(서초역);

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(line.getAllStations().size()).isEqualTo(2);
        assertThat(line.getAllStations().stream().map(Station::getName).collect(Collectors.toList())).contains("강남역","교대역");
    }
}
