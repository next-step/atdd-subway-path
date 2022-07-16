package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.*;
import static org.assertj.core.api.Assertions.assertThat;

class PersistedLineTest {


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
        line.addSection(section(line, station1, station2));
        line.addSection(section(line, station3, station1));

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
        line.addSection(section(line, station1, station2));
        line.addSection(section(line, station3, station1));
        line.addSection(section(line, station4, station3));

        final List<Station> result = line.getStations();

        // then
        assertThat(result).containsExactly(station4, station3, station1, station2);
    }

}
