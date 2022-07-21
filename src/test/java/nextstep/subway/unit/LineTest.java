package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.LineTestSources.line;
import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.station;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void getStations_Empty() {
        final Line line = new Line();

        // when
        final List<Station> result = line.getStations();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void getStations_3Stations() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        // when (3, 1), (1, 2) -> (1, 2), (3, 1)
        line.addSection(section(station1, station2));
        line.addSection(section(station3, station1));

        final List<Station> result = line.getStations();

        // then
        assertThat(result).containsExactly(station3, station1, station2);
    }

    @Test
    void getStations_4Stations() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        // when (4, 3), (3, 1), (1, 2) -> (1, 2), (3, 1), (4, 3)
        line.addSection(section(station1, station2));
        line.addSection(section(station3, station1));
        line.addSection(section(station4, station3));

        final List<Station> result = line.getStations();

        // then
        assertThat(result).containsExactly(station4, station3, station1, station2);
    }

    @Test
    void 마지막구간은삭제불가() {
        final Line line = line();
        line.addSection(section(station(1L), station(2L)));


        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> line.removeSection(station(2L)));

        assertThat(result).hasMessageContaining("Last section cannot be removed");
    }

    @Test
    void 마지막구간이아니지만존재하지않는역이면삭제불가능() {
        final Line line = line();
        line.addSection(section(station(1L), station(2L)));


        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> line.removeSection(station(3L)));

        assertThat(result).hasMessageContaining("Station not exists");
    }

    @Test
    void 마지막구간이아니며존재하는역은삭제가능() {
        final Line line = line();
        final Station downStation = station(2L);
        line.addSection(section(station(1L), downStation));
        final Station lastStation = station(3L);
        line.addSection(section(downStation, lastStation));

        line.removeSection(lastStation);
    }

}
