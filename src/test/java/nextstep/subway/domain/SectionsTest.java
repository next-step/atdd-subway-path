package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.station;
import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    private Sections target;

    @BeforeEach
    void setUp() {
        target = new Sections();
    }

    @Test
    void 처음역삭제() {
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        target.add(section(station1, station2));
        target.add(section(station2, station3));

        target.remove(station1);

        final List<Station> stations = target.findAllStationsInOrder();
        assertThat(stations).doesNotContain(station1);
        assertThat(stations).containsExactly(station2, station3);
    }

    @Test
    void 마지막역삭제() {
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        target.add(section(station1, station2));
        target.add(section(station2, station3));

        target.remove(station3);

        final List<Station> stations = target.findAllStationsInOrder();
        assertThat(stations).doesNotContain(station3);
        assertThat(stations).containsExactly(station1, station2);
    }

    @Test
    void 중간역삭제_역3개() {
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        target.add(section(station1, station2, 10));
        target.add(section(station2, station3, 15));

        target.remove(station2);

        final List<Station> stations = target.findAllStationsInOrder();
        assertThat(stations).doesNotContain(station2);
        assertThat(stations).containsExactly(station1, station3);
        assertThat(findTotalDistance(target)).isEqualTo(25);
    }

    @Test
    void 중간역삭제_역4개() {
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        target.add(section(station1, station2, 10));
        target.add(section(station2, station3, 15));
        target.add(section(station3, station4, 15));

        target.remove(station3);

        final List<Station> stations = target.findAllStationsInOrder();
        assertThat(stations).doesNotContain(station3);
        assertThat(stations).containsExactly(station1, station2, station4);
        assertThat(findTotalDistance(target)).isEqualTo(40);
    }

    private int findTotalDistance(final Sections sections) {
        return sections.getSections().stream().mapToInt(Section::getDistance).sum();
    }

}