package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.StationTestSources.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {

    @Test
    void addSection_BetweenDownStation() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        line.addSection(station1, station3, 10);
        line.addSection(station3, station4, 10);

        // when
        line.addSection(station2, station3, 4);

        // then
        final List<Section> sections = line.getSections();
        assertSections(station1, station2, 6, sections.get(0));
        assertSections(station2, station3, 4, sections.get(1));
    }

    @Test
    void addSection_BetweenDownStationOverLength() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        line.addSection(station1, station3, 10);
        line.addSection(station3, station4, 10);

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> line.addSection(station2, station3, 100));

        // then
        assertThat(result).isNotNull();
        assertThat(line.getSections()).hasSize(2);
    }

    @Test
    void addSection_BetweenUpStation() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        line.addSection(station1, station2, 10);
        line.addSection(station2, station4, 10);

        // when
        line.addSection(station2, station3, 4);

        // then
        final List<Section> sections = line.getSections();

        assertSections(station2, station3, 4, sections.get(1));
        assertSections(station3, station4, 6, sections.get(2));
    }

    @Test
    void addSection_BetweenUpStationOverLength() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        line.addSection(station1, station2, 10);
        line.addSection(station2, station4, 10);

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> line.addSection(station2, station3, 100));

        // then
        assertThat(result).isNotNull();
        assertThat(line.getSections()).hasSize(2);
    }

    private void assertSections(final Station upStation, final Station downStation, final int distance, final Section section) {
        assertThat(section.getUpStation()).isEqualTo(upStation);
        assertThat(section.getDownStation()).isEqualTo(downStation);
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    @Test
    void addSection_FirstUpStation() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        line.addSection(station2, station3, 10);

        // when
        line.addSection(station1, station2, 10);

        // then
        assertThat(line.getStations()).containsExactly(station1, station2, station3);
    }

    @Test
    void addSection_LastDownStation() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        line.addSection(station1, station2, 10);

        // when
        line.addSection(station2, station3, 10);

        // then
        assertThat(line.getStations()).containsExactly(station1, station2, station3);
    }

    @Test
    void addSection_Empty() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);

        // when
        line.addSection(station1, station2, 10);

        // then
        assertThat(line.getStations()).containsExactly(station1, station2);
    }

    @Test
    void update_null() {
        // given
        final String name = "name";
        final String color = "color";
        final Line line = new Line(name, color);

        // when
        line.update(null, null);

        // then
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @Test
    void update_notnull() {
        // given
        final String name = "name";
        final String color = "color";
        final Line line = new Line(null, null);

        // when
        line.update(name, color);

        // then
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @Test
    void isLastDownStationTrue() {
        // given
        final Line line = new Line();
        final Station downStation = downStation();
        line.addSection(upStation(), downStation, 3);

        // when
        final boolean result = line.isLastDownStation(downStation);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void isLastDownStationFalse() {
        // given
        final Line line = new Line();
        final Station upStation = upStation();
        line.addSection(upStation, downStation(), 3);

        // when
        final boolean result = line.isLastDownStation(upStation);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void removeLastSection() {
        // given
        final Line line = new Line();
        final Station downStation = downStation();
        line.addSection(upStation(), downStation, 3);

        // when
        line.removeLastSection();

        // then
        assertThat(line.getSections()).isEmpty();
    }

}
