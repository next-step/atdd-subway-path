package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.*;
import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

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
        line.addSection(section(line, upStation(), downStation));

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
        line.addSection(section(line, upStation, downStation()));

        // when
        final boolean result = line.isLastDownStation(upStation);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void isFirstStationFalse() {
        // given
        final Line line = new Line();
        final Station downStation = downStation();
        line.addSection(section(line, upStation(), downStation));

        // when
        final boolean result = line.isFirstStation(downStation);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void isFirstStationTrue() {
        // given
        final Line line = new Line();
        final Station upStation = upStation();
        line.addSection(section(line, upStation, downStation()));

        // when
        final boolean result = line.isLastDownStation(upStation);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void hasStationFalse() {
        // given
        final Line line = new Line();
        final Station downStation = downStation();
        line.addSection(section(line, upStation(), downStation));

        // when
        final boolean result = line.hasStation(downStation);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void hasStationTrue() {
        // given
        final Line line = new Line();
        line.addSection(section(line, upStation(), downStation()));

        // when
        final boolean result = line.hasStation(station(10));

        // then
        assertThat(result).isFalse();
    }

    @Test
    void removeLastSection() {
        // given
        final Line line = new Line();
        final Station downStation = downStation();
        line.addSection(section(line, upStation(), downStation));

        // when
        line.removeLastSection();

        // then
        assertThat(line.getSections()).isEmpty();
    }

}
