package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.StationTestSources.downStation;
import static nextstep.subway.utils.StationTestSources.upStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LineTest {

    @Test
    void addSection() {
        // given
        final Line line = new Line();

        // when
        line.addSection(mock(Station.class), mock(Station.class), 10);

        // then
        assertThat(line.getSections()).isNotEmpty();
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

    @Test
    void getFirstUpStation() {
        // given
        final Line line = new Line();
        final Station upStation = upStation();
        line.addSection(upStation, downStation(), 3);

        // when
        final Station result = line.getFirstUpStation();

        // then
        assertThat(result).isEqualTo(upStation);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
