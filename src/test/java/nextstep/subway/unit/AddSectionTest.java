package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.station;
import static org.assertj.core.api.Assertions.assertThat;

public class AddSectionTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line();
    }

    @Test
    void 구간추가_맨뒤() {
        final Station station1 = station(1L);
        final Station station2 = station(2L);
        final Station station3 = station(3L);

        line.addSection(section(station1, station2));
        line.addSection(section(station2, station3));
        assertThat(line.getStations()).containsExactly(station1, station2, station3);
        assertThat(line.getStations()).containsExactly(station1, station2, station3);
    }

    @Test
    void 구간추가_맨앞() {
        final Station station1 = station(1L);
        final Station station2 = station(2L);
        final Station station3 = station(3L);

        line.addSection(section(station2, station3));
        line.addSection(section(station1, station2));
        assertThat(line.getStations()).containsExactly(station1, station2, station3);
    }

    @Test
    void 구간추가_중간1() {
        final Station station1 = station(1L);
        final Station station2 = station(2L);
        final Station station4 = station(4L);

        // (2, 3) + 2
        // (1, 2) 3 + (2, 4) 6 --> (1, 2) 3 + (2, 3) 2  + (3, 4) 4
        line.addSection(section(station2, station4, 3));
        line.addSection(section(station1, station2, 6));

        final Station station3 = station(3L);
        line.addSection(section(station2, station3, 2));

        assertThat(line.getStations()).containsExactly(station1, station2, station3, station4);
        assertThat(line.getDistance()).isEqualTo(9);
    }

    @Test
    void 구간추가_중간3() {
        final Station station1 = station(1L);
        final Station station3 = station(3L);
        final Station station4 = station(4L);

        // (2, 3) + 2
        // (1, 3) 6 + (3, 4) 3 --> (1, 2) 4 + (2, 3) 2  + (3, 4) 3
        line.addSection(section(station3, station4, 3));
        line.addSection(section(station1, station3, 6));

        final Station station2 = station(2L);
        line.addSection(section(station2, station3, 2));

        assertThat(line.getStations()).containsExactly(station1, station2, station3, station4);
        assertThat(line.getDistance()).isEqualTo(9);
    }

}
