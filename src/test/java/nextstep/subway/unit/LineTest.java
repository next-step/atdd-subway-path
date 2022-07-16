package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.StationTestSources.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LineTest {

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
